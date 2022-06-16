package com.ski.tournament.core;


import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.shared.Registration;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

class TwinColGridListAdapterModified<T> implements HasValue<ValueChangeEvent<List<T>>, List<T>> {

    @NotNull
    private final TwinColGridModified<T> delegate;

    public void setValue(List<T> value) {
        this.delegate.setValue(new LinkedHashSet(value));
    }

    public List<T> getValue() {
        return Collections.unmodifiableList((List)this.delegate.collectValue(Collectors.toList()));
    }

    public Registration addValueChangeListener(ValueChangeListener<? super ValueChangeEvent<List<T>>> listener) {
        List<Registration> registrations = new ArrayList();
        registrations.add(this.delegate.addValueChangeListener((ev) -> {
            List<T> value = new ArrayList((Collection)ev.getValue());
            ValueChangeEvent<List<T>> listEvent = new com.ski.tournament.core.TwinColGridListAdapterModified.ValueChangeEventImpl(ev.isFromClient(), new ArrayList(value));
            listener.valueChanged(listEvent);
        }));
        registrations.add(this.delegate.getRightGrid().addSortListener((ev) -> {
            List<T> value = this.getValue();
            ValueChangeEvent<List<T>> listEvent = new com.ski.tournament.core.TwinColGridListAdapterModified.ValueChangeEventImpl(ev.isFromClient(), value);
            listener.valueChanged(listEvent);
        }));
        return () -> {
            registrations.forEach(Registration::remove);
        };
    }

    public List<T> getEmptyValue() {
        return Collections.emptyList();
    }

    public TwinColGridListAdapterModified(@NotNull TwinColGridModified<T> delegate) {
        if (delegate == null) {
            throw new NullPointerException("delegate is marked non-null but is null");
        } else {
            this.delegate = delegate;
        }
    }

    public boolean isEmpty() {
        return this.delegate.isEmpty();
    }

    public void clear() {
        this.delegate.clear();
    }

    public void setReadOnly(boolean readOnly) {
        this.delegate.setReadOnly(readOnly);
    }

    public boolean isReadOnly() {
        return this.delegate.isReadOnly();
    }

    public void setRequiredIndicatorVisible(boolean requiredIndicatorVisible) {
        this.delegate.setRequiredIndicatorVisible(requiredIndicatorVisible);
    }

    public boolean isRequiredIndicatorVisible() {
        return this.delegate.isRequiredIndicatorVisible();
    }

    private final class ValueChangeEventImpl implements ValueChangeEvent<List<T>> {
        boolean isFromClient;
        List<T> value;

        public HasValue<?, List<T>> getHasValue() {
            return com.ski.tournament.core.TwinColGridListAdapterModified.this;
        }

        public List<T> getOldValue() {
            return null;
        }

        public boolean isFromClient() {
            return this.isFromClient;
        }

        public List<T> getValue() {
            return this.value;
        }

        public ValueChangeEventImpl(boolean isFromClient, List<T> value) {
            this.isFromClient = isFromClient;
            this.value = value;
        }
    }

    private interface IDelegate {
        boolean isEmpty();

        void clear();

        void setReadOnly(boolean var1);

        boolean isReadOnly();

        void setRequiredIndicatorVisible(boolean var1);

        boolean isRequiredIndicatorVisible();
    }
}