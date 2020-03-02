package dynamicore.input.middlewares;

public interface DataInputMiddleware {
    /**
     * predefined events with predefined parameters
     *
     * @param event
     * @param params
     */
    void event(Event event, Object[] params);

    /**
     * custom event from external
     *
     * @param event
     * @param params
     */
    void event(String event, Object[] params);
}
