package br.com.zenon;

import br.com.zenon.fraud.TransactionReport;
import br.com.zenon.fraud.TransactionReport.SummaryReport;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;
import java.util.ResourceBundle;

public class ReportMain {
    public static void main(String[] args) {
        Locale locale = Locale.of("pt");

        if (args.length > 0) {
            String lang = args[0].trim().toLowerCase();
            locale = ("en".equals(lang)) ? Locale.US : Locale.of("pt");
        }

        ResourceBundle bundle = ResourceBundle.getBundle("report", locale);
        NumberFormat integerFormat = NumberFormat.getIntegerInstance(locale);
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(locale);
        currencyFormat.setCurrency(Currency.getInstance("USD"));

        String fileName = "data/PS_20174392719_1491204439457_log.csv";
        TransactionReport report = new TransactionReport(fileName);

        SummaryReport summary = report.generateSummaryReport();

        String totalLinesLabel = bundle.getString("label.total.lines");
        String totalFraudsLabel = bundle.getString("label.total.frauds");
        String totalAmountLabel = bundle.getString("label.total.amount");
        String reportSummaryLabel = """
                %s: %s
                %s: %s
                %s: %s""";

        String formatTotalLines = integerFormat.format(summary.totalLines());
        String formatTotalFrauds = integerFormat.format(summary.totalFrauds());
        String formatTotalAmount = currencyFormat.format(summary.totalAmount());

        String formattedAmount = currencyFormat.format(summary.totalAmount());

        IO.println(reportSummaryLabel.formatted(
                        totalLinesLabel, formatTotalLines,
                        totalFraudsLabel, formatTotalFrauds,
                        totalAmountLabel, formatTotalAmount));

    }
}
