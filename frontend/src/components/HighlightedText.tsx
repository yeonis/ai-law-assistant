type HighlightedTextProps = {
    text: string;
    keyword: string;
};

function escapeRegExp(value: string) {
    return value.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
}

function HighlightedText({ text, keyword }: HighlightedTextProps) {

    if(!keyword){
        return <>{text}</>;
    }
    const escapedKeywords = keyword.trim()
        .split(/\s+/)
        .map(escapeRegExp);

    const regex = new RegExp(`(${escapedKeywords.join('|')})`, 'gi');
    const parts = text.split(regex);

    return (
        <>
            {parts.map((part, index) => (
                <span key={index}>
                    {index % 2 === 1 ? (
                        <mark>{part}</mark>
                    ) : (
                        part
                    )}
                </span>
            ))}
        </>
);
}
export default HighlightedText;