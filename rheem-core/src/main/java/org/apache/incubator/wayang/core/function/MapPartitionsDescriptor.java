package io.rheem.rheem.core.function;

import io.rheem.rheem.core.optimizer.ProbabilisticDoubleInterval;
import io.rheem.rheem.core.optimizer.costs.LoadProfileEstimator;
import io.rheem.rheem.core.types.BasicDataUnitType;
import io.rheem.rheem.core.types.DataUnitType;

import java.util.Optional;
import java.util.function.Function;

/**
 * This descriptor pertains to functions that consume and output multiple data quanta.
 *
 * @param <Input>  input type of the transformation function
 * @param <Output> output type of the transformation function
 */
public class MapPartitionsDescriptor<Input, Output> extends FunctionDescriptor {

    protected final BasicDataUnitType<Input> inputType;

    protected final BasicDataUnitType<Output> outputType;

    private final SerializableFunction<Iterable<Input>, Iterable<Output>> javaImplementation;

    /**
     * The selectivity ({code 0..*}) of this instance or {@code null} if unspecified.
     */
    private ProbabilisticDoubleInterval selectivity;

    public MapPartitionsDescriptor(SerializableFunction<Iterable<Input>, Iterable<Output>> javaImplementation,
                                   Class<Input> inputTypeClass,
                                   Class<Output> outputTypeClass) {
        this(javaImplementation, inputTypeClass, outputTypeClass, (ProbabilisticDoubleInterval) null);
    }

    public MapPartitionsDescriptor(SerializableFunction<Iterable<Input>, Iterable<Output>> javaImplementation,
                                   Class<Input> inputTypeClass,
                                   Class<Output> outputTypeClass,
                                   ProbabilisticDoubleInterval selectivity) {
        this(javaImplementation, inputTypeClass, outputTypeClass, selectivity, null);
    }


    public MapPartitionsDescriptor(SerializableFunction<Iterable<Input>, Iterable<Output>> javaImplementation,
                                   Class<Input> inputTypeClass,
                                   Class<Output> outputTypeClass,
                                   LoadProfileEstimator loadProfileEstimator) {
        this(javaImplementation,
                inputTypeClass,
                outputTypeClass,
                null,
                loadProfileEstimator);
    }

    public MapPartitionsDescriptor(SerializableFunction<Iterable<Input>, Iterable<Output>> javaImplementation,
                                   Class<Input> inputTypeClass,
                                   Class<Output> outputTypeClass,
                                   ProbabilisticDoubleInterval selectivity,
                                   LoadProfileEstimator loadProfileEstimator) {
        this(javaImplementation,
                DataUnitType.createBasic(inputTypeClass),
                DataUnitType.createBasic(outputTypeClass),
                selectivity,
                loadProfileEstimator);
    }

    public MapPartitionsDescriptor(SerializableFunction<Iterable<Input>, Iterable<Output>> javaImplementation,
                                   BasicDataUnitType<Input> inputType,
                                   BasicDataUnitType<Output> outputType,
                                   ProbabilisticDoubleInterval selectivity,
                                   LoadProfileEstimator loadProfileEstimator) {
        super(loadProfileEstimator);
        this.javaImplementation = javaImplementation;
        this.inputType = inputType;
        this.outputType = outputType;
        this.selectivity = selectivity;
    }

    /**
     * This is function is not built to last. It is thought to help out devising programs while we are still figuring
     * out how to express functions in a platform-independent way.
     *
     * @return a function that can perform the reduce
     */
    public Function<Iterable<Input>, Iterable<Output>> getJavaImplementation() {
        return this.javaImplementation;
    }

    /**
     * In generic code, we do not have the type parameter values of operators, functions etc. This method avoids casting issues.
     *
     * @return this instance with type parameters set to {@link Object}
     */
    @SuppressWarnings("unchecked")
    public MapPartitionsDescriptor<Object, Object> unchecked() {
        return (MapPartitionsDescriptor<Object, Object>) this;
    }

    public BasicDataUnitType<Input> getInputType() {
        return this.inputType;
    }

    public BasicDataUnitType<Output> getOutputType() {
        return this.outputType;
    }

    /**
     * Get the selectivity of this instance.
     *
     * @return an {@link Optional} with the selectivity or an empty one if no selectivity was specified
     */
    public Optional<ProbabilisticDoubleInterval> getSelectivity() {
        return Optional.ofNullable(this.selectivity);
    }

    @Override
    public String toString() {
        return String.format("%s[%s]", this.getClass().getSimpleName(), this.javaImplementation);
    }
}