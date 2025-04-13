package io.cdap.wrangler.directives.aggregate;

import io.cdap.wrangler.api.Arguments;
import io.cdap.wrangler.api.Directive;
import io.cdap.wrangler.api.DirectiveContext;
import io.cdap.wrangler.api.ExecutorContext;
import io.cdap.wrangler.api.Row;
import io.cdap.wrangler.api.annotation.Name;
import io.cdap.wrangler.api.annotation.Description;
import io.cdap.wrangler.api.annotation.Staged;
import io.cdap.wrangler.api.annotation.Staged.Direction;

import java.util.ArrayList;
import java.util.List;

@Name("aggregate-size-time")
@Description("Aggregates byte sizes and time durations, supporting total or average with optional unit conversions.")
@Staged(direction = Direction.ROWS)
public class AggregateSizeTime implements Directive {

    private String sizeCol;
    private String timeCol;
    private String resultSizeCol;
    private String resultTimeCol;
    private String sizeUnit;
    private String timeUnit;
    private String aggType;

    public AggregateSizeTime() {
        // Required for Wrangler to instantiate directive
    }

    @Override
    public void initialize(DirectiveContext context, Arguments args) {
        sizeCol = args.value("sizeColumn");
        timeCol = args.value("timeColumn");
        resultSizeCol = args.value("resultSizeColumn");
        resultTimeCol = args.value("resultTimeColumn");
        sizeUnit = args.optional("sizeUnit", "B"); // Options: B, KB, MB, GB
        timeUnit = args.optional("timeUnit", "ns"); // Options: ns, ms, s, min
        aggType = args.optional("aggType", "total"); // Options: total, average
    }

    @Override
    public List<Row> execute(List<Row> rows, ExecutorContext context) {
        long totalSize = 0L;
        long totalTime = 0L;
        int count = 0;

        for (Row row : rows) {
            Object sizeObj = row.getValue(sizeCol);
            Object timeObj = row.getValue(timeCol);
            if (sizeObj != null && timeObj != null) {
                try {
                    long size = parseBytes(sizeObj.toString());
                    long time = parseDuration(timeObj.toString());
                    totalSize += size;
                    totalTime += time;
                    count++;
                } catch (NumberFormatException e) {
                    // skip malformed row
                }
            }
        }

        if ("average".equalsIgnoreCase(aggType) && count > 0) {
            totalSize /= count;
            totalTime /= count;
        }

        double convertedSize = convertSize(totalSize, sizeUnit);
        double convertedTime = convertTime(totalTime, timeUnit);

        List<Row> output = new ArrayList<>();
        Row result = new Row();

        if (resultSizeCol != null) {
            result.add(resultSizeCol, convertedSize);
        }
        if (resultTimeCol != null) {
            result.add(resultTimeCol, convertedTime);
        }
        output.add(result);
        return output;
    }

    private long parseBytes(String value) {
        value = value.trim().toUpperCase();
        try {
            if (value.endsWith("GB"))
                return (long) (Double.parseDouble(value.replace("GB", "")) * 1024 * 1024 * 1024);
            if (value.endsWith("MB"))
                return (long) (Double.parseDouble(value.replace("MB", "")) * 1024 * 1024);
            if (value.endsWith("KB"))
                return (long) (Double.parseDouble(value.replace("KB", "")) * 1024);
            if (value.endsWith("B"))
                return (long) (Double.parseDouble(value.replace("B", "")));
            return Long.parseLong(value); // fallback if no unit
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private long parseDuration(String value) {
        value = value.trim().toLowerCase();
        try {
            if (value.endsWith("min"))
                return (long) (Double.parseDouble(value.replace("min", "")) * 60 * 1_000_000_000L);
            if (value.endsWith("s"))
                return (long) (Double.parseDouble(value.replace("s", "")) * 1_000_000_000L);
            if (value.endsWith("ms"))
                return (long) (Double.parseDouble(value.replace("ms", "")) * 1_000_000L);
            if (value.endsWith("ns"))
                return (long) (Double.parseDouble(value.replace("ns", "")));
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private double convertSize(long bytes, String unit) {
        switch (unit.toUpperCase()) {
            case "KB":
                return bytes / 1024.0;
            case "MB":
                return bytes / (1024.0 * 1024);
            case "GB":
                return bytes / (1024.0 * 1024 * 1024);
            default:
                return (double) bytes; // B
        }
    }

    private double convertTime(long nanos, String unit) {
        switch (unit.toLowerCase()) {
            case "ms":
                return nanos / 1_000_000.0;
            case "s":
                return nanos / 1_000_000_000.0;
            case "min":
                return nanos / (60.0 * 1_000_000_000);
            default:
                return (double) nanos; // ns
        }
    }
}
