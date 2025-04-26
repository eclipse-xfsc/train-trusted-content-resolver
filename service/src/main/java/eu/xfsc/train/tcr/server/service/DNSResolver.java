package eu.xfsc.train.tcr.server.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.xbill.DNS.DClass;
import org.xbill.DNS.DohResolver;
import org.xbill.DNS.ExtendedResolver;
import org.xbill.DNS.Flags;
import org.xbill.DNS.Message;
import org.xbill.DNS.Name;
import org.xbill.DNS.PTRRecord;
import org.xbill.DNS.RRset;
import org.xbill.DNS.Rcode;
import org.xbill.DNS.Record;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.Section;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.TXTRecord;
import org.xbill.DNS.Type;
import org.xbill.DNS.URIRecord;
import org.xbill.DNS.dnssec.ValidatingResolver;

import eu.xfsc.train.tcr.server.exception.DnsException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("dns-resolver")
public class DNSResolver implements HealthIndicator {

	private static final String PREFIX = "_scheme._trust.";
	private final static String DNSROOT = ". IN DNSKEY 257 3 8 AwEAAaz/tAm8yTn4Mfeh5eyI96WSVexTBAvkMgJzkKTOiW1vkIbzxeF3+/4RgWOq7HrxRixHlFlExOLAJr5emLvN7SWXgnLh4+B5xQlNVz8Og8kvArMtNROxVQuCaSnIDdD5LKyWbRd2n9WGe2R8PzgCmr3EgVLrjyBxWezF0jLHwVN8efS3rCj/EWgvIWgb9tarpVUDK/b58Da+sqqls3eNbuv7pr+eoZG+SrDK6nWeL3c6H5Apxz7LjVc1uTIdsIXxuOLYA4/ilBmSVIzuDWfdRUfhHdY6+cn8HFRm+2hM8AnXGXws9555KrUB5qihylGa8subX2Nn6UwNR1AkUTV74bU=";

	@Autowired
	private Resolver resolver;
	@Value("${tcr.dns.dnssec.enabled}")
	private boolean dnssecEnabled;
	@Value("${tcr.dns.dnssec.rootPath}")
	private String dnssecRootPath;

	private Resolver finalResolver;

	@Override
	public Health health() {
		Health.Builder health;
		List<String> hosts = null;
		try {
			boolean resolved = false;
			if (resolver instanceof SimpleResolver) {
				Pair<String, Boolean> rrr = resolveResolver(resolver);
				hosts = List.of(rrr.getLeft());
				resolved = rrr.getRight();
			} else { // (rs instanceof ExtendedResolver) {
				Resolver[] rss = ((ExtendedResolver) resolver).getResolvers();
				hosts = new ArrayList<>(rss.length);
				for (Resolver rs : rss) {
					Pair<String, Boolean> rrr = resolveResolver(rs);
					hosts.add(rrr.getLeft());
					if (rrr.getRight()) {
						resolved = rrr.getRight();
					}
				}
			}
			health = resolved ? Health.up() : Health.outOfService();
		} catch (Exception ex) {
			log.warn("health.error: ", ex);
			health = Health.down(ex);
		}
		return health.withDetail("hosts", hosts).withDetail("dnsSecEnabled", dnssecEnabled).build();
	}

	@PostConstruct
	public void init() throws IOException {
		log.info("init; initializing DNS with resolver: {}, DNSSEC: {}", resolver, dnssecEnabled);
		if (dnssecEnabled) {
			InputStream rootInputStream = null;
			if (!StringUtils.isBlank(dnssecRootPath)) {
				File f = new File(dnssecRootPath);
				if (f.exists() && !f.isDirectory()) {
					rootInputStream = new FileInputStream(dnssecRootPath);
				} else {
					rootInputStream = getClass().getClassLoader().getResourceAsStream(dnssecRootPath);
				}
			}
			if (rootInputStream == null) {
				log.info("resolve; DNSSEC Root-key not found, using hardcoded backup.");
				rootInputStream = new ByteArrayInputStream(DNSROOT.getBytes());
			}

			ValidatingResolver validatingResolver = new ValidatingResolver(resolver);
			validatingResolver.loadTrustAnchors(rootInputStream);
			finalResolver = validatingResolver;
		} else {
			finalResolver = resolver;
		}
	}

	private Pair<String, Boolean> resolveResolver(Resolver res) {
		if (res instanceof SimpleResolver) {
			SimpleResolver sr = (SimpleResolver) res;
			return Pair.of(sr.getAddress().getAddress().getHostAddress() + ":" + sr.getAddress().getPort(),
					!sr.getAddress().isUnresolved());
		}
		DohResolver dr = (DohResolver) res;
		return Pair.of(dr.getUriTemplate(), true);
	}

	public Collection<String> resolveDomain(String domain) {
		log.debug("resolveDomain.enter; got domain: {}", domain);
		Set<String> processed = new HashSet<>();
		Set<String> results = resolveDomain(processed, domain);
		log.debug("resolveDomain.exit; returning: {}", results);
		return results;
	}

	private Set<String> resolveDomain(Set<String> processed, String domain) {
		domain = fixUri(domain);
		if (processed.contains(domain)) {
			return Collections.emptySet();
		}

		Set<String> uris;
		try {
			uris = resolvePtr(domain);
		} catch (IOException ex) {
			throw new DnsException(ex);
		}
		processed.add(domain);
		List<Exception> errors = new ArrayList<>();
		Set<String> results = new HashSet<>();
		for (String uri : uris) {
			try {
				results.addAll(resolveUri(uri));
			} catch (IOException ex) {
				log.warn("resolveDomain; error processing URI [{}]: {}", uri, ex.getMessage());
				errors.add(ex);
			}
			results.addAll(resolveDomain(processed, uri));
		}
		if (results.isEmpty() && !errors.isEmpty()) {
			// collect all exceptions into one?
			throw new DnsException(errors.get(0));
		}
		return results;
	}

	/**
	 * resolves DNS PTR record
	 * 
	 * @param ptr - the PTR record to query
	 * @return the resolved Set<String>
	 */
	private Set<String> resolvePtr(String ptr) throws IOException {
		log.debug("resolvePtr.enter; got PTR: {}", ptr);
		Set<String> set = query(ptr, Type.PTR).filter(r -> r instanceof PTRRecord)
				.map(r -> ((PTRRecord) r).getTarget().toString()).collect(Collectors.toSet());
		log.debug("resolvePtr.exit; returning uris: {}", set);
		return set;
	}

	/**
	 * resolves DNS URI record
	 * 
	 * @param ptr - the URI record to query
	 * @return the resolved Set<String>
	 */
	private Set<String> resolveUri(String uri) throws IOException {
		log.debug("resolveUri.enter; got URI: {}", uri);
		Set<String> set = query(uri, Type.URI).filter(r -> r instanceof URIRecord).map(r -> ((URIRecord) r).getTarget())
				.collect(Collectors.toSet());
		log.debug("resolveUri.exit; returning dids: {}", set);
		return set;
	}

	/**
	 * 
	 * @param uri  - the domain to be queried
	 * @param type - DNS record type
	 * @return the stream of DNS records
	 * @throws IOException
	 */
	private Stream<Record> query(String uri, int type) throws IOException {
		Record query = Record.newRecord(Name.fromConstantString(uri), type, DClass.IN);
		log.debug("query; DNS query: {}", query);
		Message response = finalResolver.send(Message.newQuery(query));
		// log.debug("query; response: {}", response);

		int rcode = response.getRcode();
		log.debug("query; got RCode: {} ({}); AD Flag present: {}", rcode, Rcode.string(rcode),
				response.getHeader().getFlag(Flags.AD));
		if (rcode != Rcode.NOERROR) {
			// the code below is just to log an additional info about error condition
			for (RRset rrSet : response.getSectionRRsets(Section.ADDITIONAL)) {
				log.debug("query; Zone: {}", rrSet.getName());
				if (rrSet.getName().equals(Name.root) && rrSet.getType() == Type.TXT
						&& rrSet.getDClass() == ValidatingResolver.VALIDATION_REASON_QCLASS) {
					log.info("query; Reason: {}", ((TXTRecord) rrSet.first()).getStrings().get(0));
				}
			}
			return Stream.empty();
		}

		List<RRset> rrSets = response.getSectionRRsets(Section.ANSWER);
		return rrSets.stream().flatMap(s -> s.rrs().stream());
	}

	private String fixUri(String uri) {
		if (!uri.startsWith(PREFIX)) {
			uri = PREFIX + uri;
		}
		if (!uri.endsWith(".")) {
			uri = uri + ".";
		}
		return uri;
	}

}
