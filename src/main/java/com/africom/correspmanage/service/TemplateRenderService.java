package com.africom.correspmanage.service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

@Service
public class TemplateRenderService {

    public String render(String templateContent, Map<String, Object> variables) {
        if (templateContent == null || templateContent.isBlank()) {
            return "";
        }
        try {
            String decoded = decodeHtmlEditor(templateContent);

            Configuration cfg = new Configuration(Configuration.VERSION_2_3_33);
            cfg.setDefaultEncoding("UTF-8");
            Template template = new Template("emailTemplate", new StringReader(decoded), cfg);
            StringWriter writer = new StringWriter();
            template.process(variables, writer);
            return writer.toString();
        } catch (IOException | TemplateException e) {
            throw new RuntimeException("Erreur lors du rendu FreeMarker : " + e.getMessage(), e);
        }
    }

    private String decodeHtmlEditor(String editorContent) {
        // Remplacement manuel des entités HTML — sans dépendance commons-text
        String decoded = editorContent
            .replace("&lt;",   "<")
            .replace("&gt;",   ">")
            .replace("&amp;",  "&")
            .replace("&quot;", "\"")
            .replace("&#39;",  "'")
            .replace("&apos;", "'")
            .replace("&#34;",  "\"")
            .replace("&#60;",  "<")
            .replace("&#62;",  ">")
            .replace("&nbsp;", " ");

        // Supprimer les balises <p> et </p> wrappantes ajoutées par le dx-html-editor
        decoded = decoded
            .replace("<p>",  "")
            .replace("</p>", "\n")
            .replace("<br>", "\n")
            .replace("<br/>", "\n")
            .replace("<br />", "\n");

        // Nettoyer les lignes vides multiples
        decoded = decoded.replaceAll("\\n{3,}", "\n\n");

        return decoded.trim();
    }
}
