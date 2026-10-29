
import HighlightedText from './HighlightedText';

type ArticleContentProps = {
    content: string;
    keyword: string;
};

const imagePattern = /<img\b[^>]*>[\s\S]*?<\/img\s*>/gi;
const srcPattern = /src=["']([^"']+)["']/i;

function ArticleContent({ content, keyword }: ArticleContentProps) {

    // 이미지 블록을 기준으로 본문을 분리
    const parts = content.split(imagePattern);

    // 원문에서 이미지 블록 추출
    const images = [...content.matchAll(imagePattern)];

    function getImageUrl(imageBlock: string) {
        const match = imageBlock.match(srcPattern);
        if (!match) return null;

        try {
            const url = new URL(match[1]);

            if (
                !['http:', 'https:'].includes(url.protocol) ||
                !['law.go.kr', 'www.law.go.kr'].includes(url.hostname) ||
                url.pathname !== '/flDownload.do' ||
                !/^\d+$/.test(url.searchParams.get('flSeq') ?? '') ||
                [...url.searchParams.keys()].some(key => key !== 'flSeq')
            ) {
                return null;
            }

            url.protocol = 'https:';
            return url.toString();
        } catch {
            return null;
        }
    }

    return (
        <>
            {parts.map((part, index) => {
                const imageBlock = images[index]?.[0];
                const imageUrl = imageBlock
                    ? getImageUrl(imageBlock)
                    : null;

                return (
                    <span key={index}>
                    <HighlightedText
                        text={part}
                        keyword={keyword}
                    />

                        {imageUrl && (
                            <img
                                src={imageUrl}
                                alt="법령 표 이미지"
                                className="article-image"
                                loading="lazy"
                            />
                        )}

                        {imageBlock && !imageUrl && (
                            <HighlightedText
                                text={imageBlock}
                                keyword={keyword}
                            />
                        )}
                </span>
                );
            })}
        </>
    );
}

export default ArticleContent;
