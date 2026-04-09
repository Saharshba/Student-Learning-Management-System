(function(){
    const storageKey = 'lmsTheme';
    const toggleClass = 'theme-toggle';
    const icons = { dark: '☀', light: '🌙' };

    function setMode(mode) {
        const isDark = mode === 'dark';
        document.body.classList.toggle('dark-mode', isDark);
        localStorage.setItem(storageKey, isDark ? 'dark' : 'light');
        const button = document.querySelector('.' + toggleClass);
        if (button) button.textContent = isDark ? icons.dark : icons.light;
    }

    function init() {
        const saved = localStorage.getItem(storageKey);
        if (saved === 'dark') {
            document.body.classList.add('dark-mode');
        }
        const button = document.querySelector('.' + toggleClass);
        if (!button) return;
        button.textContent = document.body.classList.contains('dark-mode') ? icons.dark : icons.light;
        button.addEventListener('click', function() {
            setMode(document.body.classList.contains('dark-mode') ? 'light' : 'dark');
        });
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }
})();