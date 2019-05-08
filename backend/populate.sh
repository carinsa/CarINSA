#!/bin/bash
srv="http://localhost/DEV/parkings/"

pkgids=(6 72 97 95 83 51 17 66 88 45 8 60 22 28 34 55 50 3 71 2 59 26 33 38 54 65 79 69 21 20 82 64 58 53 32 44 87 19 16 75 91 93 94 47 76 68 90 74 92 15 40 39 62 11 25 27 80 37 10 1 67 43 57 46 31 35 30 24 36 86 61 4 77 85 23 96 42 63 18 81 78 49 9 70 29 52 56 48 73 89 41 98 13 99 100 12 101 7 5 14 84)

for pkgid in "${pkgids[@]}"
do
	echo "Parking "$pkgid
	for run in {1..30}
	do
		rating=$((RANDOM % 4))
		uid=$(cat /dev/urandom | tr -dc 'a-zA-Z0-9' | fold -w 20 | head -n 1)
		#echo $uid
		url=$srv"setRating.php?u="$uid"&p="$pkgid"&r="$rating
		echo -n " "$url"... "
		wget $url -O /dev/null > /dev/null 2>&1
		echo "OK"
	done
done
