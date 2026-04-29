package com.africom.correspmanage.service;

import com.africom.correspmanage.client.OrgaCareRestClient;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TemplateRenderService {

    private final Logger log = LoggerFactory.getLogger(TemplateRenderService.class);
    private final OrgaCareRestClient orgaCareRestClient;

    public TemplateRenderService(OrgaCareRestClient orgaCareRestClient) {
        this.orgaCareRestClient = orgaCareRestClient;
    }

    public String render(String templateContent, Map<String, Object> variables) {
        if (templateContent == null || templateContent.isBlank()) {
            return "";
        }
        try {
            String decoded = decodeHtmlEditor(templateContent);

            // ← NOUVEAU : pré-traiter les variables ${emailDestinataire-TYPE}
            decoded = resolveAffectationEmailVariables(decoded, variables);

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

    /**
     * Résout les patterns ${emailDestinataire-TYPE} dans le template AVANT le rendu FreeMarker.
     * Exemple : ${emailDestinataire-CHEF} → "chef1@example.com, chef2@example.com"
     * Nécessite que "departementId" soit présent dans les variables du contexte.
     */
    private String resolveAffectationEmailVariables(String template, Map<String, Object> variables) {
        // Pattern qui matche ${emailDestinataire-CHEF}, ${emailDestinataire-MEMBRE}, etc.
        Pattern pattern = Pattern.compile("\\$\\{emailDestinataire-([A-Z_]+)\\}");
        Matcher matcher = pattern.matcher(template);

        // Si aucune occurrence → retourner directement
        if (!matcher.find()) {
            return template;
        }

        Object depIdObj = variables.get("departementId");
        if (depIdObj == null) {
            log.warn("Variable 'departementId' absente du contexte — les variables emailDestinataire-TYPE seront remplacées par vide");
            return pattern.matcher(template).replaceAll("");
        }

        Long departementId;
        try {
            departementId = Long.valueOf(depIdObj.toString());
        } catch (NumberFormatException e) {
            log.warn("departementId invalide dans le contexte : {}", depIdObj);
            return pattern.matcher(template).replaceAll("");
        }

        // Réinitialiser le matcher depuis le début
        matcher.reset();
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String typeStr = matcher.group(1); // ex: "CHEF", "MEMBRE", "ASSISTANT", "INTERIM"
            String replacement = "";
            try {
                List<String> emails = orgaCareRestClient
                    .getEmailsByDepartementAndType(departementId, typeStr);
                if (emails != null && !emails.isEmpty()) {
                    replacement = String.join(", ", emails);
                }
                log.debug("Résolution ${emailDestinataire-{}} → {} emails trouvés",
                    typeStr, emails != null ? emails.size() : 0);
            } catch (Exception e) {
                log.warn("Erreur résolution emailDestinataire-{} pour deptId={} : {}",
                    typeStr, departementId, e.getMessage());
            }
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    private String decodeHtmlEditor(String editorContent) {
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

        decoded = decoded
            .replace("<p>",    "")
            .replace("</p>",   "\n")
            .replace("<br>",   "\n")
            .replace("<br/>",  "\n")
            .replace("<br />", "\n");

        decoded = decoded.replaceAll("\\n{3,}", "\n\n");
        return decoded.trim();
    }
}
