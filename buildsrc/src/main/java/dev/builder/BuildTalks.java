package dev.builder;

import org.gradle.api.Project;

public final class BuildTalks {
    private BuildTalks() {
    }

    public static void attach(Project project) {
        project.afterEvaluate(evaluated -> evaluated.getLogger().lifecycle(
            "[buildSrc] {} -> Minecraft {}, Fabric Loader {}, Java {}",
            evaluated.getName(),
            MetaLibs.property(evaluated, "minecraft_version"),
            MetaLibs.property(evaluated, "loader_version"),
            MetaLibs.TARGET_JAVA_VERSION
        ));
    }
}
