package br.com.zenon;

import br.com.zenon.fraud.TransactionReport;
import br.com.zenon.fraud.TransactionReport.SummaryReport;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class ReportMain {
    public static void main(String[] args) {
        Locale locale = Locale.of("pt", "BR");
        if (args.length > 0) {
            String lang = args[0].trim().toLowerCase();
            if ("en".equals(lang) || "en_us".equals(lang) || "english".equals(lang) || "ingles".equals(lang) || "inglês".equals(lang)) {
                locale = Locale.US;
            } else if ("pt".equals(lang) || "pt_br".equals(lang) || "portuguese".equals(lang) || "portugues".equals(lang) || "português".equals(lang)) {
                locale = Locale.of("pt", "BR");
            }
        }

        ResourceBundle bundle = ResourceBundle.getBundle("report", locale);
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(locale);

        String fileName = "data/PS_20174392719_1491204439457_log.csv";
        TransactionReport report = new TransactionReport(fileName);

        SummaryReport summary = report.generateSummaryReport();

        String totalLinesLabel = bundle.getString("total.lines");
        String totalFraudsLabel = bundle.getString("total.frauds");
        String totalAmountLabel = bundle.getString("total.amount");
        String formattedAmount = currencyFormat.format(summary.totalAmount());

        System.out.println(totalLinesLabel + ": " + summary.totalLines() +
                " | " + totalFraudsLabel + ": " + summary.totalFrauds() +
                " | " + totalAmountLabel + ": " + formattedAmount);
    }
}
