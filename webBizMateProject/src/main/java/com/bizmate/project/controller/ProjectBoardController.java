package com.bizmate.project.controller;

import com.bizmate.project.dto.request.ProjectBoardRequestDTO;
import com.bizmate.project.service.ProjectBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/project/board")
@RequiredArgsConstructor
public class ProjectBoardController {

    private final ProjectBoardService projectBoardService;

    public Map<String, String> registet(@RequestBody ProjectBoardRequestDTO requestDTO){
        projectBoardService.register(requestDTO);
        return Map.of("RESULT","SUCCESS");
    }

//    @GetMapping("/{id}")
//    public Map<String , String > getBoard (@PathVariable(name = "id")Long id){
//        return
//    }

}
