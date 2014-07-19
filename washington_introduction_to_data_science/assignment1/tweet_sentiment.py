#!/usr/bin/env python

import sys
import json

def hw(sent_file, tweet_file):
    scores = {}     # initialize an empty dictionary
    with open(sent_file) as f:
        for line in f:
            term, score = line.split("\t")
            scores[term] = int(score)

    with open(tweet_file) as f:
        for line in f:
            tweet = json.loads(line.strip())
            score = 0
            if tweet.has_key(u'text'):
                encoded_string = tweet['text'].encode('utf-8')
                for word in encoded_string.split():
                    if word in scores.keys():
                        score = score + scores[word]
            print score

def lines(fp):
    print str(len(fp.readlines()))

def main():
    sent_file = sys.argv[1]
    tweet_file = sys.argv[2]
    hw(sent_file, tweet_file)

if __name__ == '__main__':
    main()
