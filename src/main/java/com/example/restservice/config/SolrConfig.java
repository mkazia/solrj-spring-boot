package com.example.restservice.config;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.config.Lookup;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.mapping.SimpleSolrMappingContext;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;


@Configuration
@EnableSolrRepositories(basePackages = "com.example.restservice.repository")
public class SolrConfig {

     @Autowired
     private ApplicationContext appContext;

    @Value("${solr.zkEnsemble}")
    private String zkEnsemble;

    @Value("${solr.username}")
    String username;

    @Value("${solr.password}")
    String password;

    @Bean
    public SolrClient solrClient() {

        String[] zk = zkEnsemble.split("/");
        List zkList = Arrays.asList(zk[0].split(","));
        String zkRoot = "/" + zk[1];

        HttpClientUtil.setCookiePolicy(SolrPortAwareCookieSpecFactory.POLICY_NAME);
        HttpClientUtil.getHttpClientBuilder()
                .setDefaultCredentialsProvider(() -> {
                    CredentialsProvider credsProvider = new BasicCredentialsProvider();
                    credsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
                    return credsProvider;
                })
                .setCookieSpecRegistryProvider(() -> {
                    SolrPortAwareCookieSpecFactory cookieFactory = new SolrPortAwareCookieSpecFactory();
                    Lookup<CookieSpecProvider> cookieRegistry = RegistryBuilder.<CookieSpecProvider> create()
                            .register(SolrPortAwareCookieSpecFactory.POLICY_NAME, cookieFactory).build();

                    return cookieRegistry;
                });

        SolrClient client = new CloudSolrClient.Builder(zkList, Optional.of(zkRoot)).build();
        //SolrClient client = new HttpSolrClient.Builder("https://localhost:8983/solr").build();

        return client;

    }

    @Bean
    public SolrTemplate solrTemplate(SolrClient client) throws Exception {
    final SimpleSolrMappingContext solrMappingContext = new SimpleSolrMappingContext();
        solrMappingContext.setApplicationContext(appContext);
	SolrTemplate solrTemplate = new SolrTemplate(client);
	solrTemplate.setMappingContext(solrMappingContext);
        return solrTemplate;
    }

    @Bean(name = "collection")
    public String collection(@Value("${solr.collection}") String collection){
        return collection;
    }
}
