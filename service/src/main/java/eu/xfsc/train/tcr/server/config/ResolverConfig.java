package eu.xfsc.train.tcr.server.config;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.xbill.DNS.DohResolver;
import org.xbill.DNS.ExtendedResolver;
import org.xbill.DNS.SimpleResolver;

import com.apicatalog.jsonld.JsonLdError;
import com.apicatalog.jsonld.document.Document;
import com.apicatalog.jsonld.document.JsonDocument;
import com.apicatalog.jsonld.loader.DocumentLoader;
import com.danubetech.verifiablecredentials.jsonld.VerifiableCredentialContexts;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import org.xbill.DNS.Resolver;

import eu.xfsc.train.tcr.server.exception.DidException;
import eu.xfsc.train.tcr.server.exception.DnsException;
import eu.xfsc.train.tcr.server.model.TrustListWithHash;
import foundation.identity.did.DIDDocument;
import foundation.identity.jsonld.ConfigurableDocumentLoader;
import lombok.extern.slf4j.Slf4j;
import uniresolver.UniResolver;
import uniresolver.client.ClientUniResolver;
import uniresolver.local.LocalUniResolver;

@Slf4j
@Configuration
public class ResolverConfig {

	@Bean
	public UniResolver uniResolver(@Value("${tcr.did.base-uri}") String baseUri,
			@Value("${tcr.did.config-path}") String configPath) {
		log.info("uniResolver.enter; configured base URI: {}, config path: {}", baseUri, configPath);
		UniResolver resolver;
		if ("local".equalsIgnoreCase(baseUri) && !StringUtils.isBlank(configPath)) {
			try {
			    resolver = LocalUniResolver.fromConfigFile(configPath);
			} catch (IOException ex) {
				log.error("uniResolver.error", ex);
				// fallback with baseUri..?
				throw new DidException(ex);
			}
		} else {
			URI uri;
			try {
				uri = new URI(baseUri);
			} catch (URISyntaxException ex) {
				log.error("uniResolver.error", ex);
				throw new DidException(ex);
			}
			resolver = ClientUniResolver.create(uri);
		}
		log.info("uniResolver.exit; returning resolver: {}", resolver);
		return resolver;
	}

	@Bean
	public Resolver resolver(@Value("${tcr.dns.doh.enabled}") boolean dohEnabled,
			@Value("${tcr.dns.hosts}") List<String> dnsHosts,
			@Value("${tcr.dns.timeout}") int timeout) {
		log.info("resolver.enter; configured DNS hosts: {}, timeout: {}", dnsHosts, timeout);
		Resolver resolver;
		try {
			if (dnsHosts.isEmpty()) {
				resolver = new SimpleResolver();
			} else {
				Resolver[] resolvers = new Resolver[dnsHosts.size()];
				int idx = 0;
				for (String dnsHost: dnsHosts) {
					Resolver r = dohEnabled ? new DohResolver(dnsHost) : new SimpleResolver(dnsHost);
					if (timeout > 0) {
						r.setTimeout(Duration.ofMillis(timeout));
					}
					resolvers[idx++] = r;
				}
				resolver = new ExtendedResolver(resolvers);
			}
			if (timeout > 0) {
				resolver.setTimeout(Duration.ofMillis(timeout));
			}
		} catch (IOException ex) {
			log.error("resolver.error", ex);
			throw new DnsException(ex);
		}
		log.info("resolver.exit; returning resolver: {}", resolver);
		return resolver;
	}

	@Bean
	public DocumentLoader documentLoader(Cache<URI, Document> docLoaderCache) {
		log.info("documentLoader.enter; cache: {}", docLoaderCache);
		ConfigurableDocumentLoader loader = (ConfigurableDocumentLoader) VerifiableCredentialContexts.DOCUMENT_LOADER;
		// TODO: get settings from app props
		loader.setEnableFile(true);
		loader.setEnableHttp(true);
		loader.setEnableHttps(true);
		loader.setEnableLocalCache(false);
		loader.setRemoteCache(docLoaderCache);
		try {
			docLoaderCache.put(new URI("https://schema.org"), JsonDocument.of(new StringReader("{\"@context\": {}}")));
		} catch (URISyntaxException | JsonLdError ex) {
			log.info("documentLoader.error", ex);
		}
		log.info("documentLoader.exit; returning: {}", loader);
		return loader;
	}
	
	@Bean
	public Cache<String, DIDDocument> didDocumentCache(@Value("${tcr.did.cache.size}") int cacheSize,
			@Value("${tcr.did.cache.timeout}") Duration timeout) {
		log.info("didDocumentCache.enter; cache size: {}, ttl: {}", cacheSize, timeout);
        Caffeine<?, ?> cache = Caffeine.newBuilder().expireAfterAccess(timeout); 
        if (cacheSize > 0) {
            cache = cache.maximumSize(cacheSize);
        } 
		log.info("didDocumentCache.exit; returning: {}", cache);
        return (Cache<String, DIDDocument>) cache.build();
	}
	
	@Bean
	public Cache<URI, Document> docLoaderCache(@Value("${tcr.did.cache.size}") int cacheSize,
			@Value("${tcr.did.cache.timeout}") Duration timeout) {
		log.info("docLoaderCache.enter; cache size: {}, ttl: {}", cacheSize, timeout);
        Caffeine<?, ?> cache = Caffeine.newBuilder().expireAfterAccess(timeout); 
        if (cacheSize > 0) {
            cache = cache.maximumSize(cacheSize);
        } 
        //if (synchronizer != null) {
        //    cache = cache.removalListener(new DataListener<>(synchronizer));
        //}
		log.info("docLoaderCache.exit; returning: {}", cache);
        return (Cache<URI, Document>) cache.build();
	}
	
	@Bean
	public Cache<String, TrustListWithHash> trustListCache(@Value("${tcr.tl.cache.size}") int cacheSize,
			@Value("${tcr.tl.cache.timeout}") Duration timeout) {
		log.info("trustListCache.enter; cache size: {}, ttl: {}", cacheSize, timeout);
        Caffeine<?, ?> cache = Caffeine.newBuilder().expireAfterAccess(timeout); 
        if (cacheSize > 0) {
            cache = cache.maximumSize(cacheSize);
        } 
		log.info("trustListCache.exit; returning: {}", cache);
        return (Cache<String, TrustListWithHash>) cache.build();
	}

}

