package chess.result;
import com.google.gson.Gson;

public record RegisterResult(String username, String authToken) {
    public String toJson() {
        return new Gson().toJson(this);
    }
}
