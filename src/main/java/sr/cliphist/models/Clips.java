package sr.cliphist.models;

import java.util.List;
import java.util.function.Consumer;

public class Clips {

  private Consumer<List<String>> clipsConsumer;

  public Clips(Consumer<List<String>> clipsConsumer) {
    super();
    this.clipsConsumer = clipsConsumer;
  }

  private void setClips(List<String> clips) {

  }


}
