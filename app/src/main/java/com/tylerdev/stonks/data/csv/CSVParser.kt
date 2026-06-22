package com.tylerdev.stonks.data.csv

import java.io.InputStream

/**
 * Contract for deserialising CSV payloads into typed rows.
 *
 * Used by repositories to turn raw [InputStream] responses (e.g. Alpha Vantage listing exports)
 * into model objects ready for mapping and persistence.
 */
interface CSVParser<T> {

    /**
     * Reads and parses CSV rows from the supplied stream.
     *
     * @param stream Raw CSV input, typically from a remote HTTP response body.
     * @return Parsed rows of type [T]; an empty list when the stream contains no data rows.
     */
    suspend fun parser(stream: InputStream): List<T>
}