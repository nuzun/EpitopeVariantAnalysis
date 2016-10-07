# /home/naz/Programs/netCTLpan-1.1/Linux_i686/bin/netCTLpan
# Sun Dec  1 15:38:55 2013
# User: naz
# PWD : /home/naz/Programs/netCTLpan-1.1
# Host: Linux ubuntu 3.2.0-56-generic i686
# Command line parameters set to:
#	[-rdir filename]     /home/naz/Programs/netCTLpan-1.1/Linux_i686 Home directory for NetMHpan
#	[-c filename]        /home/naz/Programs/netCTLpan-1.1/Linux_i686/bin/clepred Cleavage prediction code
#	[-t filename]        /home/naz/Programs/netCTLpan-1.1/Linux_i686/bin/tapmat_pred_fsa Tap prediciton code
#	[-m filename]        /home/naz/Programs/netCTLpan-1.1/netMHCpan-2.3//netMHCpan MHC binding prediction code
#	[-v]                 0                    Verbose mode
#	[-dirty]             0                    Dirty mode, leave tmp dir+files
#	[-tdir filename]     /home/naz/tmp/tmpNetCTLpan Temporary directory (Default $$)
#	[-hlaseq filename]                        File with full length HLA sequences
#	[-a line]            HLA-A02:01           HLA allele
#	[-f filename]                             File name with input
#	[-s int]             -1                   Sort output on score: 0 [comb], 1 [MHC], 2 [Cle], 3 [TAP] <0 No sort
#	[-l int]             9                    Peptide length [8-11]
#	[-xls]               0                    Save output to xls file
#	[-xlsfile filename]  NetCTLpan_out.xls    Filename for xls dump
#	[-thr float]         -99.900002           Threshold for output
#	[-listMHC]           0                    Print list of alleles included in netMHCpan
#	[-thrfmt filename]   /home/naz/Programs/netCTLpan-1.1/Linux_i686/data/threshold/%s.thr Format for threshold filenames
#	[-wt float]          0.025000             Weight of tap
#	[-wc float]          0.225000             Weight of Clevage
#	[-ethr float]        1.000000             Threshold for epitopes
#	[-version filename]  /home/naz/Programs/netCTLpan-1.1/Linux_i686/data/version File with version information
#You most specify input file either by using -f or as /home/naz/Programs/netCTLpan-1.1/Linux_i686/bin/netCTLpan [args] fastafile
#Usage: netCTLpan [-h] [args] [fastafile]#
#> ../netCTLpan test.fsa > test.fsa.myout
#> ../netCTLpan -hlaseq B0702.fsa test.fsa >test.fsa_HLASEQ.myout


#!/bin/bash 
# 1 = Sort output on score: 0 [comb], 1 [MHC], 2 [Cle], 3 [TAP] <0 No sort
# 2 = peptide length
# 3 = hla allele name
# 4 = fasta file
# 5 = output result file
#

scripts/netCTLpan -s $1 -l $2 -a $3 $4 > $5

