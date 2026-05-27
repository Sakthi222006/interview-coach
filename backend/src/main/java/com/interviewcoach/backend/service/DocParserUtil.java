package com.interviewcoach.backend.service;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Component;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class DocParserUtil {

    public String extractText(Path filePath) {
        if (filePath == null) {
            return "";
        }

        AutoDetectParser parser = new AutoDetectParser();
        Metadata metadata = new Metadata();
        try (var stream = Files.newInputStream(filePath)) {
            ContentHandler handler = new BodyContentHandler(-1);
            parser.parse(stream, handler, metadata, new ParseContext());
            return cleanText(handler.toString());
        } catch (IOException | SAXException | TikaException ex) {
            return "";
        }
    }

    private String cleanText(String text) {
        if (text == null) {
            return "";
        }
        String cleaned = text.replaceAll("\\r\\n", " ")
                .replaceAll("\\n", " ")
                .replaceAll("\\t", " ")
                .replaceAll(" +", " ")
                .trim();
        return cleaned;
    }
}
