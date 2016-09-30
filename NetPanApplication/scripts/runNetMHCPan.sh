# /home/naz/Programs/netMHCpan-2.4/Linux_i686/bin/netMHCpan -a HLA-A01:01 -l 9 -s /home/naz/workspace/NetPanApplication/data/input/sequence/afp_P02771.fasta
# Fri Sep 16 13:35:44 2016
# User: naz
# PWD : /home/naz/workspace/NetPanApplication
# Host: Linux ubuntu 3.13.0-87-generic i686
# -a       HLA-A01:01           HLA allele
# -l       9                    Peptide length [8-11]
# -s       1                    Sort output on descending affinity
# Command line parameters set to:
#	[-rdir filename]     /home/naz/Programs/netMHCpan-2.4/Linux_i686 Home directory for NetMHpan
#	[-blsyn filename]    /home/naz/Programs/netMHCpan-2.4/Linux_i686/data/syn/bl/synlist Blosum synaps list
#	[-spsyn filename]    /home/naz/Programs/netMHCpan-2.4/Linux_i686/data/syn/sp/synlist Sparse synaps list
#	[-spblsyn filename]  /home/naz/Programs/netMHCpan-2.4/Linux_i686/data/syn/spbl/synlist SPBL synaps list
#	[-smin float]        -1.000000            Min value for sparse encoding
#	[-smax float]        1.000000             Max value for sparse encoding
#	[-bln float]         5.000000             Normalizing factor for blosum score
#	[-v]                 0                    Verbose mode
#	[-dirty]             0                    Dirty mode, leave tmp dir+files
#	[-sdev]              0                    Print sdev
#	[-tdir filename]     /home/naz/tmp/tmpNetMHCpan Temporary directory (Default $$)
#	[-hlapseudo filename] /home/naz/Programs/netMHCpan-2.4/Linux_i686/data/MHC_pseudo.dat File with HLA pseudo sequences
#	[-hlaseq filename]                        File with full length HLA sequences
#	[-block int]         50000                Block size of predictions
#	[-a line]            HLA-A01:01           HLA allele
#	[-f filename]                             File name with input
#	[-w]                 0                    w option for webface
#	[-s]                 1                    Sort output on descending affinity
#	[-p]                 0                    Use peptide input
#	[-th float]          50.000000            Threshold for high binding peptides
#	[-lt float]          500.000000           Threshold for low binding peptides
#	[-rth float]         0.100000             Rank Threshold for high binding peptides
#	[-rlt float]         1.000000             Rank Threshold for low binding peptides
#	[-l int]             9                    Peptide length [8-11]
#	[-xls]               0                    Save output to xls file
#	[-xlsfile filename]  NetMHCpan_out.xls    Filename for xls dump
#	[-t float]           -99.900002           Threshold for output
#	[-thrfmt filename]   /home/naz/Programs/netMHCpan-2.4/Linux_i686/data/threshold/%s.thr Format for threshold filenames
#	[-ic50]              0                    Print IC50 values for all alleles
#	[-whitelist filename] /home/naz/Programs/netMHCpan-2.4/Linux_i686/data/threshold/whitelist List of alleles to include IC50 values
#	[-expfix]            0                    Exclude prefix from synlist
#	[-version filename]  /home/naz/Programs/netMHCpan-2.4/Linux_i686/data/version File with version information
#	[-inptype int]       0                    Input type [0] FASTA [1] Peptide
#	[-listMHC]           0                    Print list of alleles included in netMHCpan
#$1 = allele - HLA-A02:01 	
#$2 = peptide length - 9
#$3 = fasta seq - filename
#$4 = output - filename

/home/naz/workspace/NetPanApplication/scripts/netMHCpan -a $1 -l $2 -s $3 > $4
