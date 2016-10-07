# /home/naz/Programs/netMHCII-2.2/Linux_i686/bin/netMHCII test/test.fsa
# Sat Oct  1 12:52:32 2016
# User: naz
# PWD : /home/naz/Programs/netMHCII-2.2
# Host: Linux ubuntu 3.13.0-87-generic i686
# Command line parameters set to:
#       [-a filename]        HLA-DRB10101         Allele name
#       [-d filename]        /home/naz/Programs/netMHCII-2.2/Linux_i686 Directory for executables and data
#       [-t float]           -99.900002           Threshold for output
#       [-s]                 0                    Sort output on descending affinity
#       [-w]                 0                    w option for webface
#       [-f filename]                             File name with input
#       [-tdir filename]     /home/naz/tmp/tmpNetMHCII-2.2 Temporary directory
#       [-wt float]          500.000000           Threshold for weak binders
#       [-st float]          50.000000            Threshold for strong binders
#       [-l int]             15                   Peptide length
#       [-v]                 0                    Verbose mode
#       [-dirty]             0                    Dirty mode
#       [-list]              0                    List alleles covered by NetMHCII method
#       [-p]                 0                    Use peptide input
#       [-xP1]               0                    Exclude P1 amino acids encoding
#       [-thrfmt filename]   /home/naz/Programs/netMHCII-2.2/Linux_i686/data/threshold%s/%s.thr Format for threshold filenames
#       [-exthr]             0                    Exclude % Threshold calibration
# Input is in FSA format

# Peptide length 15

#$1 = allele - DRB1_0101	
#$2 = peptide length - 15
#$3 = fasta seq - filename
#$4 = output - filename
scripts/netMHCII -a $1 -l $2 -s $3 > $4