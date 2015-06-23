
use strict;
use warnings;

my @colors = qw(blue yellow red grey);
my @sizes = qw(16 24 32 48);

foreach my $size (@sizes) {
    mkdir("${size}x${size}");
    foreach my $color (@colors) {

        # Still Version
        {
            my $source_fn = source_fn($color, 0);

            system(
                'inkscape', '-z',
                '-e', target_fn($color, $size, 0, 'png'),
                '-w', $size,
                '-h', $size,
                $source_fn,
            );

            system(
                'convert',
                target_fn($color, $size, 0, 'png'),
                '-background', 'white',
                '-alpha', 'remove',
                target_fn($color, $size, 0, 'gif'),
            );
        }

        # Anime Version
        {
            my $source_fn = source_fn($color, 1);
            my $png_fn = target_fn($color, $size, 1, 'png');

            system(
                'inkscape', '-z',
                '-e', $png_fn,
                '-w', $size,
                '-h', $size,
                $source_fn,
            );

            my $change = 90.0 / 11.0;
            my @frames = ();
            foreach my $step (0..9) {
                my $angle = int($step * $change);
                my $frame_fn = frame_fn($color, $size, $step);
                system(
                    'convert',
                    $png_fn,
                    '-background', 'white',
                    '-alpha', 'remove',
                    "-distort", "SRT", $angle,
                    $frame_fn
                );
                push @frames, $frame_fn;
            }

            system(
                'convert',
                '-delay', '5',
                @frames,
                '-loop', '0',
                target_fn($color, $size, 1, 'gif'),
            );
        }

        foreach my $unwanted_file (glob "${size}x${size}/*.png") {
            unlink($unwanted_file);
        }
    }
}

sub source_fn {
    my ($color, $anime) = @_;
    my $anime_str = $anime ? "_anime" : "_still";

    return "$color$anime_str.svg";
}

sub target_fn {
    my ($color, $size, $anime, $format) = @_;

    my $anime_str = $anime ? "_anime" : "";
    $format ||= 'gif';

    return "${size}x${size}/$color$anime_str.$format";
}

sub frame_fn {
    my ($color, $size, $step) = @_;

    return "${size}x${size}/${color}_f$step.png";
}
