const fs = require('fs');
const path = require('path');

const csvPath = path.join(__dirname, '../SaveethaGeoTag_Load_TestCases_All260.csv');
const content = fs.readFileSync(csvPath, 'utf8');
const lines = content.split('\n');

const header = lines[0];
const rows = lines.slice(1).filter(line => line.trim() !== '');

const counts = {};

rows.forEach((row, index) => {
    let parts = [];
    let insideQuote = false;
    let currentPart = '';
    for (let i = 0; i < row.length; i++) {
        let char = row[i];
        if (char === '"') {
            insideQuote = !insideQuote;
        } else if (char === ',' && !insideQuote) {
            parts.push(currentPart);
            currentPart = '';
        } else {
            currentPart += char;
        }
    }
    parts.push(currentPart);

    const screenCategory = parts[2] ? parts[2].trim() : 'Unknown';
    if (!counts[screenCategory]) {
        counts[screenCategory] = [];
    }
    counts[screenCategory].push({
        id: parts[1],
        desc: parts[3],
        line: index + 2
    });
});

console.log('Total test cases in CSV:', rows.length);
console.log('Unique Screen Categories in CSV:');
for (const [screen, tcs] of Object.entries(counts)) {
    console.log(`- ${screen}: ${tcs.length} test cases`);
}
