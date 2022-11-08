import spotifycharts.ChartsCalculator;

public class SpotifyChartsMain {
    public static void main(String[] args) {
        System.out.println("Welcome to the HvA Spotify Charts Calculator\n");

        ChartsCalculator chartsCalculator = new ChartsCalculator(19670427L);
        chartsCalculator.registerStreamedSongs(257);
        chartsCalculator.showResults();
    }
}
