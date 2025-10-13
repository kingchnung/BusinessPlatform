package com.bizmate.salesPages.report.salesTarget.controller;

import com.bizmate.salesPages.report.salesTarget.service.SalesTargetService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/salesTarget")
@RequiredArgsConstructor
public class SalesTargetController {
    private final SalesTargetService salesTargetService;
}
