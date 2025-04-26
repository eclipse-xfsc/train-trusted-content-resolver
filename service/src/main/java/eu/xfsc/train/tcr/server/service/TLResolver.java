package eu.xfsc.train.tcr.server.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.OptionalInt;

import javax.xml.transform.stream.StreamSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.github.benmanes.caffeine.cache.Cache;

import eu.xfsc.train.tcr.server.model.TrustListWithHash;
import eu.xfsc.train.tspa.model.trustlist.TrustList;
import eu.xfsc.train.tspa.model.trustlist.TrustServiceStatusList;
import io.ipfs.multihash.Multihash;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TLResolver {

	enum ContentType {
		XML, JSON, UNKNOWN
	}

	private final Unmarshaller trainListParser;

	@Autowired
	private ObjectMapper jsonMapper;
	@Autowired
	private Cache<String, TrustListWithHash> trustListCache;

	public TLResolver() throws JAXBException {
		JAXBContext trainListContext = JAXBContext.newInstance(TrustServiceStatusList.class);
		trainListParser = trainListContext.createUnmarshaller();
		// jsonMapper = new ObjectMapper();
		// jsonMapper.setPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE);
	}

	public TrustServiceStatusList resolveTL(String uri) {
		log.debug("resolveCustomTL.enter; got uri: {}", uri);
		TrustListWithHash trustListHash = getTrustListWithHash(uri);
		TrustServiceStatusList trustList = trustListHash == null ? null : trustListHash.getTrustList();
		log.debug("resolveCustomTL.exit; returning TL: {}", trustList);
		return trustList;
	}

	public TLResolveResult resolveTLHash(String uri, String hash) {
		log.debug("resolveTLHash.enter; got uri: {}, hash: {}", uri, hash);

		TrustListWithHash trustListHash = getTrustListWithHash(uri);
		if (trustListHash == null) {
			return null;
		}

		TLResolveResult tlResult = new TLResolveResult(trustListHash.getTrustList(), null, false);
		Multihash vcHash = Multihash.fromBase58(hash); // .decode(hash);
		Multihash.Type mType = vcHash.getType();
		if (trustListHash.getHash() == null) {
			try {
				String algo = getDigestType(mType);
				log.debug("resolveTLHash; hash algo is: {}", algo);
				MessageDigest md = MessageDigest.getInstance(algo);
				md.update(trustListHash.getContent().getBytes());
				Multihash tlHash = new Multihash(mType, md.digest());
				trustListHash.setHash(tlHash.toString());
				trustListHash.setContent(null);
				tlResult.setTlHash(trustListHash.getHash());
				tlResult.setHashVerified(vcHash.equals(tlHash));
				trustListCache.put(uri, trustListHash);
			} catch (Exception ex) {
				log.warn("resolveTLHash.error verifying Hash: {}", ex.getMessage());
			}
		} else {
			tlResult.setHashVerified(hash.equals(trustListHash.getHash()));
		}
		log.debug("resolveTLHash.exit; returning: {}", tlResult);
		return tlResult;
	}

	private TrustListWithHash getTrustListWithHash(String uri) {
		TrustListWithHash trustListHash = trustListCache.getIfPresent(uri);
		if (trustListHash == null) {
			log.debug("getTrustListWithHash; no cached TL for uri: {}", uri);
			try {
				String content = resolveContent(uri);
				TrustServiceStatusList trustList = parseContent(content);
				trustListHash = new TrustListWithHash(content, trustList, null);
				trustListCache.put(uri, trustListHash);
				log.debug("getTrustListWithHash; cached TL for uri: {}", uri);
			} catch (IOException | JAXBException ex) {
				log.error("getTrustList.error;", ex);
			}
		} else {
			log.debug("getTrustListWithHash; got cached TL for uri: {}", uri);
		}
		return trustListHash;
	}

	private String resolveContent(String uri) throws IOException {
		URL url = new URL(uri);
		InputStream input = url.openStream();
		return new String(input.readAllBytes(), StandardCharsets.UTF_8);
	}

	private TrustServiceStatusList parseContent(String content)
			throws JsonMappingException, JsonProcessingException, JAXBException {
		ContentType type = getDocType(content);
		if (type == ContentType.XML) {
			return (TrustServiceStatusList) trainListParser.unmarshal(new StreamSource(new StringReader(content)));
			// for future use
			//XmlMapper xmlMapper = new XmlMapper();
			//return xmlMapper.readValue(content, TrustServiceStatusList.class);
		}
		if (type == ContentType.JSON) {
			TrustList tl = jsonMapper.readValue(content, TrustList.class);
			return tl.getTrustServiceStatusList();
		}
		log.info("parseContent; got content with unknown type: {}", type);
		return null;
	}

	private ContentType getDocType(String content) {
		OptionalInt opt = content.chars().filter(c -> c == '<' || c == '{').findFirst();
		if (opt.isPresent()) {
			if (opt.getAsInt() == '<') {
				return ContentType.XML;
			} else if (opt.getAsInt() == '{') {
				return ContentType.JSON;
			}
		}
		return ContentType.UNKNOWN;
	}

	private String getDigestType(Multihash.Type mType) throws NoSuchAlgorithmException {
		switch (mType) {
			case sha1:
				return "SHA-1";
			case sha2_256:
				return "SHA-256";
			case sha2_512:
				return "SHA-512";
			case sha3_256:
				return "SHA3-256";
		}
		throw new NoSuchAlgorithmException("Unsupported MType: " + mType);
	}

	@Getter
	@Setter
	@AllArgsConstructor
	@ToString
	class TLResolveResult {

		private TrustServiceStatusList trustList;
		private String tlHash;
		private boolean hashVerified;

	}

}
