package com.example.videoexperiment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.coremedia.iso.boxes.Container;
import com.coremedia.iso.boxes.FileTypeBox;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
import com.googlecode.mp4parser.authoring.tracks.CroppedTrack;


//  Based off of samples at https://github.com/sannies/mp4parser/tree/master/examples/src/main/java/com/googlecode/mp4parser

public class MP4Util {
	public static void AppendMP4Files(String outputFile, String[] inputFiles) throws IOException {
		
		List<Movie> inputMovies = new ArrayList<Movie>();
		
		for (String file : inputFiles) {
			inputMovies.add(MovieCreator.build(file));
		}
		
        List<Track> videoTracks = new LinkedList<Track>();
        List<Track> audioTracks = new LinkedList<Track>();

        for (Movie m : inputMovies) {
            for (Track t : m.getTracks()) {
                if (t.getHandler().equals("soun")) {
                    audioTracks.add(t);
                }
                if (t.getHandler().equals("vide")) {
                    videoTracks.add(t);
                }
            }
        }

        Movie result = new Movie();

        if (audioTracks.size() > 0) {
            result.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
        }
        if (videoTracks.size() > 0) {
            result.addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));
        }

        Container out = new DefaultMp4Builder().build(result);

        FileChannel fc = new RandomAccessFile(outputFile, "rw").getChannel();
        out.writeContainer(fc);
        fc.close();
	}
	

    public static void TrimMP4(String inputFile, double startTime, double stopTime, String outputFile) throws IOException {
        
    	Movie movie = MovieCreator.build(inputFile);

        List<Track> tracks = movie.getTracks();
        movie.setTracks(new LinkedList<Track>());
        // remove all tracks we will create new tracks from the old

        double correctedStartTime = startTime;
        double correctedEngTime = stopTime;

        boolean timeCorrected = false;

        // Here we try to find a track that has sync samples. Since we can only start decoding
        // at such a sample we SHOULD make sure that the start of the new fragment is exactly
        // such a frame
        for (Track track : tracks) {
            if (track.getSyncSamples() != null && track.getSyncSamples().length > 0) {
                if (timeCorrected) {
                    // This exception here could be a false positive in case we have multiple tracks
                    // with sync samples at exactly the same positions. E.g. a single movie containing
                    // multiple qualities of the same video (Microsoft Smooth Streaming file)

                    throw new RuntimeException("The startTime has already been corrected by another track with SyncSample. Not Supported.");
                }
                correctedStartTime = correctTimeToSyncSample(track, correctedStartTime, false);
                correctedEngTime = correctTimeToSyncSample(track, correctedEngTime, true);
                timeCorrected = true;
            }
        }

        for (Track track : tracks) {
            long currentSample = 0;
            double currentTime = 0;
            double lastTime = 0;
            long startSample = -1;
            long endSample = -1;

            for (int i = 0; i < track.getSampleDurations().length; i++) {
                long delta = track.getSampleDurations()[i];


                if (currentTime > lastTime && currentTime <= correctedStartTime) {
                    // current sample is still before the new starttime
                    startSample = currentSample;
                }
                if (currentTime > lastTime && currentTime <= correctedEngTime) {
                    // current sample is after the new start time and still before the new endtime
                    endSample = currentSample;
                }
                
                lastTime = currentTime;
                currentTime += (double) delta / (double) track.getTrackMetaData().getTimescale();
                currentSample++;
            }
            movie.addTrack(new AppendTrack(new CroppedTrack(track, startSample, endSample)));
        }
        long start1 = System.currentTimeMillis();
        Container out = new DefaultMp4Builder().build(movie);
        long start2 = System.currentTimeMillis();
        FileOutputStream fos = new FileOutputStream(outputFile);
        FileChannel fc = fos.getChannel();
        out.writeContainer(fc);

        fc.close();
        fos.close();
        long start3 = System.currentTimeMillis();
        System.err.println("Building IsoFile took : " + (start2 - start1) + "ms");
        System.err.println("Writing IsoFile took  : " + (start3 - start2) + "ms");
        System.err.println("Writing IsoFile speed : " + (new File(outputFile).length() / (start3 - start2) / 1000) + "MB/s");
    }


    private static double correctTimeToSyncSample(Track track, double cutHere, boolean next) {
        double[] timeOfSyncSamples = new double[track.getSyncSamples().length];
        long currentSample = 0;
        double currentTime = 0;
        for (int i = 0; i < track.getSampleDurations().length; i++) {
            long delta = track.getSampleDurations()[i];

            if (Arrays.binarySearch(track.getSyncSamples(), currentSample + 1) >= 0) {
                // samples always start with 1 but we start with zero therefore +1
                timeOfSyncSamples[Arrays.binarySearch(track.getSyncSamples(), currentSample + 1)] = currentTime;
            }
            currentTime += (double) delta / (double) track.getTrackMetaData().getTimescale();
            currentSample++;

        }
        double previous = 0;
        for (double timeOfSyncSample : timeOfSyncSamples) {
            if (timeOfSyncSample > cutHere) {
                if (next) {
                    return timeOfSyncSample;
                } else {
                    return previous;
                }
            }
            previous = timeOfSyncSample;
        }
        return timeOfSyncSamples[timeOfSyncSamples.length - 1];
    }
}
