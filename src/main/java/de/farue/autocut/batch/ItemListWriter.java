package de.farue.autocut.batch;

import java.util.List;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;

@StepScope
public class ItemListWriter<T> implements ItemWriter<List<T>> {

    private ItemWriter<T> wrapped;

    public ItemListWriter(ItemWriter<T> wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void write(List<? extends List<T>> items) throws Exception {
        for (List<T> subList : items) {
            wrapped.write(subList);
        }
    }
}
