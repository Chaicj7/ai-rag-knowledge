package com.chaicj.http;

import com.chaicj.api.IRagService;
import com.chaicj.api.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.ollama.OllamaChatClient;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.PgVectorStore;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/rag")
public class RagController implements IRagService {

    @Autowired
    private TokenTextSplitter tokenTextSplitter;
    @Autowired
    private PgVectorStore pgVectorStore;
    @Autowired
    private RedissonClient redissonClient;


    @RequestMapping(value = "/query_rag_tag_list", method = RequestMethod.GET)
    @Override
    public Response<List<String>> queryRagTagList() {
        RList<String> ragTag = redissonClient.getList("ragTag");
        return Response.<List<String>>builder().code("0000").info("查询成功").data(ragTag).build();
    }

    @RequestMapping(value = "/upload_file", method = RequestMethod.POST)
    @Override
    public Response<String> uploadFile(@RequestParam String ragTag, @RequestParam("file") List<MultipartFile> files) {
        log.info("上传知识库开始{}", ragTag);
        for (MultipartFile file : files) {
            TikaDocumentReader documentReader = new TikaDocumentReader(file.getResource());
            List<Document> documents = documentReader.get();
            List<Document> documentList = tokenTextSplitter.apply(documents);
            documents.forEach(doc -> doc.getMetadata().put("knowledge", ragTag));
            documentList.forEach(doc -> doc.getMetadata().put("knowledge", ragTag));
            pgVectorStore.accept(documentList);
            RList<String> element = redissonClient.getList("ragTag");
            if (!element.contains(ragTag)) {
                element.add(ragTag);
            }
        }
        log.info("上传知识库完成{}", ragTag);
        return Response.<String>builder().code("0000").info("上传成功").build();
    }

}
