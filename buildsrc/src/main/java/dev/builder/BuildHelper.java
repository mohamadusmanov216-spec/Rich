package dev.builder;

import org.gradle.api.JavaVersion;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.DuplicatesStrategy;
import org.gradle.api.plugins.BasePluginExtension;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.jvm.tasks.Jar;

import java.io.File;
import java.net.URI;
import java.util.stream.Collectors;

public final class BuildHelper {
    private BuildHelper() {
    }

    public static void configureIdentity(Project project) {
        project.setVersion(MetaLibs.property(project, "mod_version"));
        project.setGroup(MetaLibs.property(project, "maven_group"));

        BasePluginExtension base = project.getExtensions().getByType(BasePluginExtension.class);
        base.getArchivesName().set(MetaLibs.property(project, "archives_base_name"));
    }

    public static void configureRepositories(Project project) {
        project.getRepositories().maven(repository -> {
            repository.setName(MetaLibs.METEOR_MAVEN_NAME);
            repository.setUrl(URI.create(MetaLibs.METEOR_MAVEN_URL));
        });
        project.getRepositories().mavenCentral();
    }

    public static File accessWidener(Project project) {
        return project.file("src/main/resources/accesswidener");
    }

    public static void createDependencyBuckets(Project project) {
        maybeCreate(project, MetaLibs.LIB_IMPL);
        maybeCreate(project, MetaLibs.MOD_IMPL);
    }

    public static void configureDependencies(Project project) {
        add(project, "minecraft", MetaLibs.minecraft(project));
        add(project, "mappings", MetaLibs.yarnMappings(project));

        addIncludedMod(project, MetaLibs.NETTY_HANDLER_PROXY);
        addIncludedMod(project, MetaLibs.NETTY_CODEC_SOCKS);

        add(project, "modImplementation", MetaLibs.fabricLoader(project));
        add(project, "modImplementation", MetaLibs.fabricApi(project));
        add(project, "implementation", MetaLibs.CAFFEINE);
        add(project, "compileOnly", MetaLibs.LOMBOK);
        add(project, "annotationProcessor", MetaLibs.LOMBOK);
        add(project, "modCompileOnly", MetaLibs.BARITONE);
        addShadedLibrary(project, MetaLibs.localLibraries(project));
        addShadedLibrary(project, MetaLibs.KOTLIN_STDLIB);
        add(project, "implementation", MetaLibs.GSON);
        add(project, "modImplementation", MetaLibs.JAVA_WEBSOCKET);
        add(project, "modImplementation", MetaLibs.OSHI_CORE);
    }

    public static void configureJava(Project project, int targetJavaVersion) {
        project.getTasks().withType(JavaCompile.class).configureEach(javaCompile -> {
            javaCompile.getOptions().setEncoding("UTF-8");
            if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible()) {
                javaCompile.getOptions().getRelease().set(targetJavaVersion);
            }
        });

        JavaPluginExtension java = project.getExtensions().getByType(JavaPluginExtension.class);
        JavaVersion required = JavaVersion.toVersion(targetJavaVersion);
        if (JavaVersion.current().compareTo(required) < 0) {
            java.getToolchain().getLanguageVersion().set(org.gradle.jvm.toolchain.JavaLanguageVersion.of(targetJavaVersion));
        }
        java.withSourcesJar();
    }

    public static void configureJar(Project project) {
        project.getTasks().named("jar", Jar.class).configure(jar -> {
            jar.from(project.file("LICENSE"), spec ->
                spec.rename(name -> name + "_" + project.getExtensions().getByType(BasePluginExtension.class).getArchivesName().get())
            );
            jar.from(project.provider(() ->
                project.getConfigurations()
                    .getByName(MetaLibs.LIB_IMPL)
                    .getFiles()
                    .stream()
                    .map(file -> file.isDirectory() ? file : project.zipTree(file))
                    .collect(Collectors.toList())
            ));
            jar.setDuplicatesStrategy(DuplicatesStrategy.EXCLUDE);
        });
    }

    public static void configurePublishing(Project project) {
        project.getExtensions().configure(PublishingExtension.class, publishing ->
            publishing.getPublications().create("mavenJava", MavenPublication.class, publication ->
                publication.from(project.getComponents().getByName("java"))
            )
        );
    }

    private static void addShadedLibrary(Project project, Object dependencyNotation) {
        add(project, MetaLibs.LIB_IMPL, dependencyNotation);
        add(project, "implementation", dependencyNotation);
    }

    private static void addIncludedMod(Project project, Object dependencyNotation) {
        add(project, MetaLibs.MOD_IMPL, dependencyNotation);
        add(project, "modImplementation", dependencyNotation);
        add(project, "include", dependencyNotation);
    }

    private static void add(Project project, String configurationName, Object dependencyNotation) {
        project.getDependencies().add(configurationName, dependencyNotation);
    }

    private static Configuration maybeCreate(Project project, String name) {
        Configuration existing = project.getConfigurations().findByName(name);
        return existing != null ? existing : project.getConfigurations().create(name);
    }
}
