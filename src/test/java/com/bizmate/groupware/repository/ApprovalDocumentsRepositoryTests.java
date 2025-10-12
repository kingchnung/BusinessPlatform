package com.bizmate.groupware.repository;

import com.bizmate.groupware.domain.ApprovalDocuments;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;


@DataJpaTest
public class ApprovalDocumentsRepositoryTests {

    @Autowired
    private ApprovalDocumentsRepository approvalDocumentsRepository;

    @Test
    public void createNewDocumentTest() {
        ApprovalDocuments doc = new ApprovalDocuments();
        long authorId = 120251001001L;
        long authorRoleId = 10L;

        doc.setDocId("TEST-DOC-20250101-001");

        doc.setTitle("왕찬웅 퇴직서");
        doc.setDocType("퇴직서");
        doc.setStatus("승인대기");

        // 모든 필수 String/Long 필드는 설정됨
        doc.setDocContentJson("[{title : 개인적인 사정, text : 나 돌아갈래}]");
        doc.setAuthorUserId(authorId);
        doc.setAuthorEmpId(220250428L);
        doc.setAuthorRoleId(authorRoleId);
        doc.setApprovalLineJson("[{\"approverId\":\"안재성\", \"order\":1},{\"approverId\":\"한유주\", \"order\":2}]");

        // 💡 수정: 마지막 남은 필드인 currentApproverIndex를 명시적으로 0으로 설정
        doc.setCurrentApproverIndex(0);


        doc.setFinalDocNumber("");

        ApprovalDocuments savedDocument = approvalDocumentsRepository.save(doc); // Line 37

        System.out.println("Saved Document ID: " + savedDocument.getDocId());


    }
}
