package faang.school.postservice.service.feed.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ListPartitionerTest {
    private final ListPartitioner listPartitioner = new ListPartitioner();

    @Test
    void testExec_successful() {
        List<List<Integer>> expectList = List.of(
                List.of(1, 2, 3),
                List.of(4, 5, 6),
                List.of(7, 8, 9)
        );
        List<Integer> list = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9);

        assertThat(listPartitioner.exec(list, 3))
                .isEqualTo(expectList);
    }
}