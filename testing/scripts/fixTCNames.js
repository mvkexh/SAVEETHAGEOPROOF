const fs = require('fs');
const path = require('path');

const appiumDir = path.resolve(__dirname, '../appium-tests');
const files = fs.readdirSync(appiumDir).filter(f => f.endsWith('.test.js'));

files.forEach(file => {
  const filePath = path.join(appiumDir, file);
  let content = fs.readFileSync(filePath, 'utf8');
  let lines = content.split('\n');
  let currentTc = null;
  let updated = false;

  for (let i = 0; i < lines.length; i++) {
    const itMatch = lines[i].match(/it\(['"](TC\d+)\s*-\s*[^'"]+['"]/);
    if (itMatch) {
      currentTc = itMatch[1];
    }

    if (currentTc) {
      // Look for runTest call on this or subsequent lines
      const runTestMatch = lines[i].match(/await\s+runTest\((['"])([^'"]+)(['"])/);
      if (runTestMatch) {
        const quote = runTestMatch[1];
        const existingName = runTestMatch[2];
        if (!existingName.startsWith('TC') && !existingName.startsWith(currentTc)) {
          const newName = `${currentTc} - ${existingName}`;
          lines[i] = lines[i].replace(
            `runTest(${quote}${existingName}${quote}`,
            `runTest(${quote}${newName}${quote}`
          );
          console.log(`Updated testName in ${file}: "${existingName}" -> "${newName}"`);
          updated = true;
        }
        currentTc = null; // Reset after updating
      }
    }
  }

  if (updated) {
    fs.writeFileSync(filePath, lines.join('\n'), 'utf8');
    console.log(`Saved changes to: ${file}\n`);
  }
});
