#!/usr/bin/env python

import sys
import json

def hw(tweet_file):
    terms = {}     # initialize an empty dictionary
    total = 0.0
    with open(tweet_file) as f:
        for line in f:
            tweet = json.loads(line.strip())
            if tweet.has_key(u'text'):
                encoded_string = tweet['text'].encode('utf-8')
                for term in encoded_string.split():
                    total = total + 1
                    if term in terms.keys():
                        terms[term] = terms[term] + 1.0
                    else:
                        terms[term] = 1.0
    for k, v in terms.iteritems():
        print "{0} {1:.4f}".format(k, v / total)

def lines(fp):
    print str(len(fp.readlines()))

def main():
    tweet_file = sys.argv[1]
    hw(tweet_file)

if __name__ == '__main__':
    main()
