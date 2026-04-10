package uk.ac.city.mma.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TemplateEngine {

    private static final String TEMPLATE_DIR = "src/main/resources/templates/";

    private String html;

    /*
     Load a layout file and inject a content file into {{PAGE_CONTENT}}.
    */
    public static TemplateEngine load(String layoutFile, String contentFile) {
        TemplateEngine t = new TemplateEngine();
        String layout  = readFile(layoutFile);
        String content = readFile(contentFile);
        t.html = layout.replace("{{PAGE_CONTENT}}", content);
        return t;
    }

    /*
     Load a standalone file (e.g. login.html, dashboards).
    */
    public static TemplateEngine load(String file) {
        TemplateEngine t = new TemplateEngine();
        t.html = readFile(file);
        return t;
    }

    private static String readFile(String filename) {
        try {
            return new String(Files.readAllBytes(Paths.get(TEMPLATE_DIR + filename)));
        } catch (IOException e) {
            e.printStackTrace();
            return "<h1 class='text-danger'>Template not found: " + filename + "</h1>";
        }
    }

    public TemplateEngine set(String key, String value) {
        this.html = this.html.replace("{{" + key + "}}", value == null ? "" : value);
        return this;
    }

    public TemplateEngine set(String key, int value) {
        return set(key, String.valueOf(value));
    }

    public TemplateEngine set(String key, double value) {
        return set(key, String.valueOf(value));
    }

    public TemplateEngine set(String key, boolean value) {
        return set(key, String.valueOf(value));
    }

    /*
     Wipe any unfilled placeholders before rendering.
    */

    public TemplateEngine clearRemaining() {
        this.html = this.html.replaceAll("\\{\\{[A-Z0-9_]+\\}\\}", "");
        return this;
    }

    public String render() {
        return html;
    }
}
