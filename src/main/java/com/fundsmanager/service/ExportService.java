package fundsmanager.service;


import fundsmanager.model.FundAllocation;
import fundsmanager.model.GroupSummary;

import java.io.File;
import java.util.List;

public class ExportService {

    private final ExcelExporter excelExporter = new ExcelExporter();
    private final PdfExporter pdfExporter = new PdfExporter();

    public void exportExcel(
            File file,
            List<FundAllocation> allocations,
            List<GroupSummary> groupSummaries
    ) throws Exception {

        excelExporter.export(file, allocations, groupSummaries);
    }

    public void exportPdf(
            File file,
            List<FundAllocation> allocations,
            List<GroupSummary> groupSummaries
    ) throws Exception {

        pdfExporter.export(file, allocations, groupSummaries);
    }
}
