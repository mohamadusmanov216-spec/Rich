package dev.builder;

import org.gradle.api.Project;

import java.util.List;
import java.util.Map;

public final class MetaLibs {
    public static final int TARGET_JAVA_VERSION = 21;

    public static final String METEOR_MAVEN_NAME = "meteor-maven-snapshots";
    public static final String METEOR_MAVEN_URL = "https://maven.meteordev.org/snapshots";

    public static final String LIB_IMPL = "libImpl";
    public static final String MOD_IMPL = "modImpl";

    public static final String NETTY_HANDLER_PROXY = "io.netty:netty-handler-proxy:4.1.82.Final";
    public static final String NETTY_CODEC_SOCKS = "io.netty:netty-codec-socks:4.1.82.Final";
    public static final String CAFFEINE = "com.github.ben-manes.caffeine:caffeine:3.1.8";
    public static final String LOMBOK = "org.projectlombok:lombok:1.18.30";
    public static final String BARITONE = "meteordevelopment:baritone:1.21.4-SNAPSHOT";
    public static final String KOTLIN_STDLIB = "org.jetbrains.kotlin:kotlin-stdlib:2.0.0";
    public static final String GSON = "com.google.code.gson:gson:2.10.1";
    public static final String JAVA_WEBSOCKET = "org.java-websocket:Java-WebSocket:1.5.6";
    public static final String OSHI_CORE = "com.github.oshi:oshi-core:6.4.0";

    private MetaLibs() {
    }

    public static String minecraft(Project project) {
        return "com.mojang:minecraft:" + property(project, "minecraft_version");
    }

    public static String yarnMappings(Project project) {
        return "net.fabricmc:yarn:" + property(project, "yarn_mappings") + ":v2";
    }

    public static String fabricLoader(Project project) {
        return "net.fabricmc:fabric-loader:" + property(project, "loader_version");
    }

    public static String fabricApi(Project project) {
        return "net.fabricmc.fabric-api:fabric-api:" + property(project, "fabric_version");
    }

    public static Object localLibraries(Project project) {
        return project.fileTree(Map.of("dir", "libs", "include", List.of("*.jar")));
    }

    public static String property(Project project, String name) {
        return String.valueOf(project.property(name));
    }
}
