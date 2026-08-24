package me.videogamesm12.w2k.kernel.data;

import com.google.gson.JsonElement;
import me.videogamesm12.w2k.kernel.W2K;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public interface IEntityEntry
{
    String w2k$internalName();

    JsonElement w2k$name();

    String w2k$type();

    double w2k$x();

    double w2k$y();

    double w2k$z();

    int w2k$id();

    UUID w2k$uuid();

    String w2k$data();

    default List<Object> w2k$toTableRow()
    {
        return Arrays.asList(
                w2k$name() != null ?
                        W2K.getInstance().getDriverManager().getVersionBridge().textToString(w2k$name()) : null,  // Display Name
                w2k$type(),                                                                                       // Type
                String.format("%s, %s, %s", w2k$x(), w2k$y(), w2k$z()),                                           // Location
                w2k$id(),                                                                                         // ID
                w2k$uuid().toString()                                                                             // UUID
        );
    }
}
