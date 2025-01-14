# numeric-matrix-processor

Numeric Matrix Processor in Kotlin. Implementation of [Hyperskill project](https://hyperskill.org/projects/87).

Reads and sums two matrices.

## Usage

Reads two matrices in the following format:
* single line with dimensions, separated by spaces: `<rows> <columns>`;
* `rows` lines by `columns` numbers, separated by spaces, representing matrix items.

Outputs resulting matrix items.

    4 5
    1 2 3 4 5
    3 2 3 2 1
    8 0 9 9 1
    1 3 4 5 6
    4 5
    1 1 4 4 5
    4 4 5 7 8
    1 2 3 9 8
    1 0 0 0 1

    2 3 7 8 10
    7 6 8 9 9
    9 2 12 18 9
    2 3 4 5 7
