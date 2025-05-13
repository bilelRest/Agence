package tn.rapid_post.agence.controller;

import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tn.rapid_post.agence.entity.Douane;
import tn.rapid_post.agence.repo.douaneRepo;
import tn.rapid_post.agence.reports.Reporter;


import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Controller
@Validated
public class Printer {

    @Autowired
    private douaneRepo douaneRep;

    @Autowired
    private Reporter reporter;

    @GetMapping(value = "/print-sortie", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> generateDouaneReport(
            @RequestParam(value = "colis")String colis,
            @RequestParam(value = "reprint",required = false)boolean reprint
            ) throws JRException, IOException {

    Douane douane = douaneRep.findByNumColis(colis);
    douane.setDateSortie(LocalDate.now());
    douane.setDelivered(true);
    douaneRep.save(douane);
    List<Douane> douaneList = Collections.singletonList(douane);
    Map<String, Object> parameters = new HashMap<>();
    byte[] pdfBytes = reporter.printdelivered(parameters, douaneList, false);
    return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdfBytes);


    }
    @GetMapping("reprintdelivered")
    public ResponseEntity<byte[]> reprintdelivered(@RequestParam(value = "colis")String colis) throws JRException {
        List<Douane> douaneList=new ArrayList<>();
        if (StringUtils.hasText(colis)){
            Douane douane= douaneRep.findByNumColis(colis);
            if (douane!=null){
                douaneList.add(douane);
            }
        }
        Map<String, Object> parameters = new HashMap<>();


        byte[] pdfBytes = reporter.reprintdelivered(parameters, douaneList,false);
        return  ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);

    }
    @GetMapping("reprintnotdelivered")
    public ResponseEntity<byte[]> reprintnotdelivered(@RequestParam(value = "colis")String colis) throws JRException {
        List<Douane> douaneList=new ArrayList<>();
        if (StringUtils.hasText(colis)){
            Douane douane= douaneRep.findByNumColis(colis);
            if (douane!=null){
                douaneList.add(douane);
            }
        }
        Map<String, Object> parameters = new HashMap<>();


        byte[] pdfBytes = reporter.reprintnotdelivered(parameters, douaneList,false);
        return  ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);

    }
    @GetMapping(value = "/print-list")
    public ResponseEntity<byte[]> printLlist(@RequestParam(value = "id",required = false)String id) throws JRException, IOException {
        List<Douane> douaneList =new ArrayList<>();
        if (id!=null){
            Optional douane=douaneRep.findById(Long.parseLong(id));
            if (douane.isPresent()){
                douaneList.add((Douane) douane.get());
            }
        }else {
            douaneList=douaneRep.findByPrintedFalse();
        }

        for (Douane d:douaneList){
            d.setPrinted(true);
            douaneRep.save(d);
        }

        Map<String, Object> parameters = new HashMap<>();


        byte[] pdfBytes = reporter.printnotdelivered(parameters, douaneList,false);
        return  ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}