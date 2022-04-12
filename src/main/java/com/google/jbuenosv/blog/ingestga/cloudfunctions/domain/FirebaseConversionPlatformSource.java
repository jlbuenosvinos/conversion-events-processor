package com.google.jbuenosv.blog.ingestga.cloudfunctions.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.jbuenosv.blog.ingestga.cloudfunctions.application.exception.FirebaseProcessorException;

import java.util.logging.Logger;

/**
 * Created by jbuenosv@google.com
 */
@SuppressWarnings("unused")
public class FirebaseConversionPlatformSource implements java.io.Serializable {

    public static final Logger logger = Logger.getLogger(FirebaseConversionPlatformSource.class.getName());

    private String appId;
    private String appInstanceId;
    private String appPlatform;
    private String appStore;
    private String appVersion;

    @SuppressWarnings("unused")
    public String getAppId() {
        return appId;
    }

    public String getAppInstanceId() {
        return appInstanceId;
    }

    public String getAppPlatform() {
        return appPlatform;
    }

    public String getAppStore() {
        return appStore;
    }

    public String getAppVersion() {
        return appVersion;
    }

    private void setAppId(String appId) {
        this.appId = appId;
    }

    private void setAppInstanceId(String appInstanceId) {
        this.appInstanceId = appInstanceId;
    }

    private void setAppPlatform(String appPlatform) {
        this.appPlatform = appPlatform;
    }

    public void setAppStore(String appStore) {
        this.appStore = appStore;
    }

    private void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public static class FirebaseConversionPlatformSourceBuilder {

        private JsonNode eventJsonRootNode;
        private JsonNode userDimNode;
        private JsonNode appInfoNode;


        public FirebaseConversionPlatformSourceBuilder(JsonNode eventJsonRootNode) {
            this.eventJsonRootNode = eventJsonRootNode;
        }

        public FirebaseConversionPlatformSource build() {
            FirebaseConversionPlatformSource firebaseConversionPlatformSource =  new FirebaseConversionPlatformSource();

            try {
                logger.info("FirebaseConversionPlatformSource inits.");

                if (eventJsonRootNode != null) {
                    userDimNode = eventJsonRootNode.path("userDim");
                    if (userDimNode != null) {
                        appInfoNode = userDimNode.path("appInfo");
                        if (appInfoNode != null) {
                            firebaseConversionPlatformSource.setAppPlatform(appInfoNode.path("appPlatform").textValue());
                            firebaseConversionPlatformSource.setAppId(appInfoNode.path("appId").textValue());
                            firebaseConversionPlatformSource.setAppStore(appInfoNode.path("appStore").textValue());
                            firebaseConversionPlatformSource.setAppInstanceId(appInfoNode.path("appInstanceId").textValue());
                            firebaseConversionPlatformSource.setAppVersion(appInfoNode.path("appVersion").textValue());
                        }
                        else {
                            logger.severe("Firebase appInfo dimension is null.");
                            throw new FirebaseProcessorException("Firebase appInfo dimension is null.");
                        }
                    }
                    else {
                        logger.severe("Firebase user dimension is null.");
                        throw new FirebaseProcessorException("Unable to find the Firebase user dimension.");
                    }
                }
                else {
                    logger.severe("Unable to build FirebaseConversionPlatformSource due to eventJsonRootNode is null.");
                }

                logger.info("FirebaseConversionPlatformSource has been built.");
            }
            catch(Exception e) {
                if (e.getCause() != null && e.getCause().getStackTrace() != null) {
                    logger.severe("Unable to build the FirebaseConversionPlatformSource due to [" + e.getCause().getStackTrace() + "].");
                }
                else {
                    logger.severe("Unable to build the FirebaseConversionPlatformSource.");
                }
                throw new FirebaseProcessorException(e);
            }

            return firebaseConversionPlatformSource;
        }

    }

    private FirebaseConversionPlatformSource() {
    }

    @Override
    public String toString() {
        return "FirebaseConversionPlatformSource {" +
                "appId='" + appId + '\'' +
                ", appInstanceId='" + appInstanceId + '\'' +
                ", appPlatform='" + appPlatform + '\'' +
                ", appStore='" + appStore + '\'' +
                ", appVersion='" + appVersion + '\'' +
                '}';
    }

}
