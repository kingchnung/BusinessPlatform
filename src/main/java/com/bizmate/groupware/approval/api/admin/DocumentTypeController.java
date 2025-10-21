package com.bizmate.groupware.approval.api.admin;

import com.bizmate.groupware.approval.domain.DocumentType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/enums")
public class DocumentTypeController {

    @GetMapping("/document-types")
    public ResponseEntity<List<Map<String, String>>> getDocumentTypes() {
        List<Map<String, String>> list = Arrays.stream(DocumentType.values())
                .map(type -> Map.of(
                        "code", type.getCode(),
                        "label", type.getLabel()
                ))
                .toList();

        return ResponseEntity.ok(list);
    }
}
