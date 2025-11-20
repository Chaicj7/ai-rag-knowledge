package com.chaicj.api;

import com.chaicj.api.response.Response;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IRagService {

    Response<List<String>> queryRagTagList();

    Response<String> uploadFile(String ragTag, List<MultipartFile> files);
}
