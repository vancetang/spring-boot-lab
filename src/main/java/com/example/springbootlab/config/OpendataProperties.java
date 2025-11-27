package com.example.springbootlab.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "opendata")
public class OpendataProperties {

    private Holiday holiday;

    public static class Holiday {

        private String url;

        private String outputDir;

        public String getUrl() {

            return url;

        }

        public void setUrl(String url) {

            this.url = url;

        }

        public String getOutputDir() {

            return outputDir;

        }

        public void setOutputDir(String outputDir) {

            this.outputDir = outputDir;

        }

    }

    public Holiday getHoliday() {

        return holiday;

    }

    public void setHoliday(Holiday holiday) {

        this.holiday = holiday;

    }

}
