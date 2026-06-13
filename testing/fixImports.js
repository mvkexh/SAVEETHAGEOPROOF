const fs = require('fs');
const path = require('path');

const dirs = [
  path.resolve(__dirname, 'appium-tests'),
  path.resolve(__dirname, 'selenium-tests')
];

dirs.forEach(dir => {
  if (!fs.existsSync(dir)) {
    console.log(`Directory does not exist: ${dir}`);
    return;
  }
  const files = fs.readdirSync(dir);
  files.forEach(file => {
    if (!file.endsWith('.test.js')) return;
    const filePath = path.join(dir, file);
    let content = fs.readFileSync(filePath, 'utf8');
    
    // Fix imports by changing double-up directories to single-up
    let updated = content
      .replace(/\.\.\/\.\.\/utils\//g, '../utils/')
      .replace(/\.\.\/\.\.\/config\//g, '../config/')
      .replace(/\.\.\/\.\.\/\.env/g, '../.env');
    
    if (updated !== content) {
      fs.writeFileSync(filePath, updated, 'utf8');
      console.log(`Updated imports in: ${path.basename(dir)}/${file}`);
    }
  });
});

console.log('Import correction completed successfully!');
