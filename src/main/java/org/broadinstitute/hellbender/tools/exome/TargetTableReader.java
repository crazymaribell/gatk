package org.broadinstitute.hellbender.tools.exome;

import org.broadinstitute.hellbender.utils.SimpleInterval;
import org.broadinstitute.hellbender.utils.tsv.DataLine;
import org.broadinstitute.hellbender.utils.tsv.TableColumnCollection;
import org.broadinstitute.hellbender.utils.tsv.TableReader;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Target table file reader.
 *
 * @author Valentin Ruano-Rubio &lt;valentin@broadinstitute.org&gt;
 */
public final class TargetTableReader extends TableReader<Target> {

    public TargetTableReader(final File file) throws IOException {
        super(file);
    }

    @Override
    protected void processColumns(final TableColumnCollection columns) {
        if (!columns.containsAll(TargetColumns.COLUMN_NAME_ARRAY)) {
            throw formatException("Bad header: missing required columns: "
                    + Stream.of(TargetColumns.COLUMN_NAME_ARRAY)
                            .filter(c -> !columns.contains(c)).collect(Collectors.joining(", ")));
        }
    }

    @Override
    protected Target createRecord(final DataLine dataLine) {

        final String contig = dataLine.get(TargetColumns.CONTIG);
        final int start = dataLine.getInt(TargetColumns.START);
        final int end = dataLine.getInt(TargetColumns.END);
        if (start < 0) {
            throw formatException("the start position must not be negative: " + start);
        } else if (start > end) {
            throw formatException(String.format("the start position %d cannot be greater than the end position %d", start, end));
        }
        final SimpleInterval interval = new SimpleInterval(contig, start, end);
        final String name = dataLine.get(TargetColumns.NAME);
        return new Target(name, interval);
    }
}
