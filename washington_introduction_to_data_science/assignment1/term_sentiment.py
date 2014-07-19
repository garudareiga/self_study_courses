#!/usr/bin/env python

import sys
import json

def hw(sent_file, tweet_file):
    scores = {}     # initialize an empty dictionary
    with open(sent_file) as f:
        for line in f:
            term, score = line.split("\t")
            scores[term] = int(score)

    estimation = {}
    with open(tweet_file) as f:
        for line in f:
            tweet = json.loads(line.strip())
            if tweet.has_key(u'text'):
                score = 0
                encoded_string = tweet['text'].encode('utf-8')
                words = encoded_string.split()
                for word in words:
                    if word in scores.keys():
                        score = score + scores[word]
                for word in words:
                    if word in scores.keys():
                        continue
                    if word not in estimation.keys():
                        estimation[word] = [0.0, 0.001, 0.0]
                    estimation[word][2] = estimation[word][2] + 1.0
                    if score > 0:       # positive
                        estimation[word][0] = estimation[word][0] + 1.0
                    elif score < 0:     # negative
                        estimation[word][1] = estimation[word][1] + 1.0

    for k, v in estimation.iteritems():
        sentiment = v[0] / v[1]
        print "{0} {1:.2f}".format(k, sentiment)

def lines(fp):
    print str(len(fp.readlines()))

def main():
    hw(sys.argv[1], sys.argv[2])

if __name__ == '__main__':
    main()
