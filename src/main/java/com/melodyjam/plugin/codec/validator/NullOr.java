package com.melodyjam.plugin.codec.validator;

import com.hypixel.hytale.codec.schema.SchemaContext;
import com.hypixel.hytale.codec.schema.config.Schema;
import com.hypixel.hytale.codec.validation.LateValidator;
import com.hypixel.hytale.codec.validation.ValidationResults;
import com.hypixel.hytale.codec.validation.Validator;

import javax.annotation.Nonnull;

public class NullOr<T> implements Validator<T> {
    private final Validator<T> inner;

    public NullOr(Validator<T> inner) {
        this.inner = inner;
    }

    @Override
    public void accept(T t, ValidationResults validationResults) {
        if (t == null) {
            return;
        }

        this.inner.accept(t, validationResults);
    }

    @Override
    public void updateSchema(SchemaContext schemaContext, Schema schema) {
        this.inner.updateSchema(schemaContext, schema);
    }

    @Override
    @Nonnull
    public LateValidator<T> late() {
        return this.inner.late();
    }
}
