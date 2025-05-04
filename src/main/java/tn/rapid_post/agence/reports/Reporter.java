package tn.rapid_post.agence.reports;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;
import org.springframework.stereotype.Service;
import tn.rapid_post.agence.entity.Douane;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Service

public class Reporter {


    public byte[] reports(Map<String, Object> params, List<Douane> list) throws JRException {
        InputStream inputStream = getClass().getResourceAsStream("/report/recu.jrxml");

        if (inputStream == null) {
            System.out.println("Fichier vide ou non trouvé");
        } else {
            System.out.println("Fichier trouvé avec succès");
        }

        JRBeanArrayDataSource dataSource = new JRBeanArrayDataSource(list.toArray());
        JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
        JasperPrint print = JasperFillManager.fillReport(jasperReport, params, dataSource);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        JasperExportManager.exportReportToPdfStream(print,outputStream);

        return outputStream.toByteArray() ;
    }



}