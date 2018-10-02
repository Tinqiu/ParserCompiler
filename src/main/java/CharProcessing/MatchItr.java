package CharProcessing;

import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.regex.Matcher;

final class MatchItr extends Spliterators.AbstractSpliterator<String> {
    private final Matcher matcher;

    MatchItr(Matcher m){
        super(m.regionEnd()-m.regionStart(), ORDERED|NONNULL);
        matcher = m;
    }

    public boolean tryAdvance(Consumer<? super String> action){
        if(!matcher.find()) return false;
        action.accept(matcher.group());
        return true;
    }

}
