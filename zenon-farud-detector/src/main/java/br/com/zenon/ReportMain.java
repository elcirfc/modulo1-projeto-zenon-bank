package br.com.zenon;

import br.com.zenon.fraud.TransactionReport;
import br.com.zenon.fraud.TransactionReport.SummaryReport;

public class ReportMain {
    public static void main(String[] args) {
        String fileName = "data/PS_20174392719_1491204439457_log.csv";
        TransactionReport report = new TransactionReport(fileName);

        SummaryReport summary = report.generateSummaryReport();

        String mensagem =
                "Total de linhas: %d " +
                "Total de fraudes: %d " +
                "Valor total transacionado: %.2f";

        IO.println(mensagem.formatted(summary.totalLines(), summary.totalFrauds(), summary.totalAmount())

        );
    }
}
