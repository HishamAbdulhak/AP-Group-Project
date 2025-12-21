package groupproject.apgroupproject.services;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class IngestionService {
    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;

    public IngestionService(EmbeddingModel embeddingModel, EmbeddingStore<TextSegment> embeddingStore) {
        this.embeddingModel = embeddingModel;
        this.embeddingStore = embeddingStore;
    }

    public Document loadSingleDocument(File file) {
        return FileSystemDocumentLoader.loadDocument(file.toPath(), new ApacheTikaDocumentParser());
    }

    public List<TextSegment> splitDocument(Document document) {
        DocumentSplitter splitter = DocumentSplitters.recursive(500, 50);
        return splitter.split(document);
    }

    public void ingestFile(File file){
        Document doc = loadSingleDocument(file);
        doc.metadata().add("file_name", file.getName());

        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(DocumentSplitters.recursive(500, 50))
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();

        ingestor.ingest(doc);
    }

    public void ingestAllFiles(String folderPath) {
        File folder = new File(folderPath);
        if (!folder.exists() || !folder.isDirectory()) return;

        File[] files = folder.listFiles();
        if (files == null) return;

        List<Document> documents = new ArrayList<>();

        System.out.println("Preparing documents for Bulk Ingestion from: " + folderPath);
        for (File file : files) {
            String name = file.getName();
            String lowerName = name.toLowerCase();
            if (lowerName.endsWith(".pdf") || lowerName.endsWith(".docx") || lowerName.endsWith(".txt")) {
                try {
                    // Load the document but DON'T ingest it yet
                    Document doc = loadSingleDocument(file);
                    doc.metadata().add("file_name", file.getName());
                    documents.add(doc);
                    System.out.println("   - Prepared: " + file.getName());
                } catch (Exception e) {
                    System.err.println("    Failed to load: " + file.getName());
                }
            }
        }
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(DocumentSplitters.recursive(500, 50))
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();
        if (!documents.isEmpty()) {
            ingestor.ingest(documents);
        }

        System.out.println(" Bulk Ingestion Complete. Loaded " + documents.size() + " documents.");
    }

}