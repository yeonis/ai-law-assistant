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
    const escapedKeyword = escapeRegExp(keyword);
    const regex = new RegExp(`(${escapedKeyword})`, 'gi');
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