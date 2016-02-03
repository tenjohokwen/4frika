package org.fourfrika.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.endpoint.InfoEndpoint;
import org.springframework.boot.bind.PropertiesConfigurationFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
public class InfoPropertiesConfiguration {
    @Autowired
    private final ConfigurableEnvironment environment = new StandardEnvironment();

    @Value("${spring.git.properties:classpath:git.properties}")
    private Resource gitProperties;

    @Bean
    public InfoEndpoint infoEndpoint() throws Exception {
        LinkedHashMap<String, Object> info = new LinkedHashMap<>();

        //Load with info from properties (all info prefixes from ppty files)
        info.putAll(infoMap());

        //load git information
        GitInfo gitInfo = gitInfo();
        if (gitInfo.getBranch() != null) {
            info.put("git", gitInfo);
        }

        info.put("active Profiles", environment.getActiveProfiles());
        return new InfoEndpoint(info);
    }

    private GitInfo gitInfo() throws Exception {
        PropertiesConfigurationFactory<GitInfo> factory = new PropertiesConfigurationFactory<>(
                new GitInfo());
        factory.setTargetName("git");
        Properties properties = new Properties();
        if (this.gitProperties.exists()) {
            properties = PropertiesLoaderUtils.loadProperties(this.gitProperties);
        }
        factory.setProperties(properties);
        return factory.getObject();
    }

    private Map<String, Object> infoMap() throws Exception {
        PropertiesConfigurationFactory<Map<String, Object>> factory = new PropertiesConfigurationFactory<>(
                new LinkedHashMap<>());
        factory.setTargetName("info");
        factory.setPropertySources(this.environment.getPropertySources());
        return factory.getObject();
    }

    public static class GitInfo {

        private String branch;

        private Boolean dirty;

        private final Build build = new Build();

        private final Commit commit = new Commit();

        public String getBranch() {
            return this.branch;
        }

        public void setBranch(String branch) {
            this.branch = branch;
        }

        public Commit getCommit() {
            return this.commit;
        }
        public Build getBuild() {
            return this.build;
        }

        public Boolean getDirty() {
            return dirty;
        }

        public void setDirty(Boolean dirty) {
            this.dirty = dirty;
        }

        public static class Commit {

            private String id;

            private String time;

            public String getId() {
                return this.id == null ? ""
                        : (this.id.length() > 7 ? this.id.substring(0, 7) : this.id);
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getTime() {
                return this.time;
            }

            public void setTime(String time) {
                this.time = time;
            }

        }

        public static class Build {
            private String version;
            private String time;

            public String getVersion() {
                return version;
            }

            public void setVersion(String version) {
                this.version = version;
            }

            public String getTime() {
                return time;
            }

            public void setTime(String time) {
                this.time = time;
            }
        }
    }
}
