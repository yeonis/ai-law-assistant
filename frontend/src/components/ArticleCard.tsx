import type { LawArticle } from '../types/law';
import HighlightedText from './HighlightedText';
import ArticleContent from './ArticleContent';

type ArticleCardProps = {
    article: LawArticle;
    expandedId: number | null;
    expandAll: boolean;
    setExpandedId: (id: number | null) => void;
    keyword: string;
    onCompare: (
        currentMst: number,
        articleNo: number,
        branchNo: number
    ) => void;
    compareLoading: boolean;
};


function ArticleCard({
                         article,
                         expandedId,
                         expandAll,
                         setExpandedId,
                         keyword,
                         onCompare,
                         compareLoading
                     }: ArticleCardProps) {

    const cleanContent = article.articleContent
        .replace(/&#x20;/gi, ' ')
        .replace(/&nbsp;/gi, ' ')
        .replace(/&#160;/g, ' ');

    return (
        <div className="article-card">
            <div className="article-meta">
                {article.lawName} · 제{article.articleNo}
                {article.articleBranchNo > 0
                    ? `조의${article.articleBranchNo}`
                    : '조'}
            </div>
            <div className="article-title-row">
                <h3><HighlightedText
                    text={article.articleTitle}
                    keyword={keyword}
                    />
                </h3>

                <button
                    type="button"
                    className="compare-button"
                    disabled={compareLoading}
                    onClick={(e) => {
                        e.stopPropagation();

                        onCompare(
                            article.lawSerialNo,
                            article.articleNo,
                            article.articleBranchNo
                        );
                    }}
                >
                    ⚖ 개정 전후 비교
                </button>
            </div>
            <div className={
                (expandAll || article.articleId === expandedId)
                    ? "article-content expanded"
                    : "article-content preview"
            }>
                <ArticleContent
                    content={
                        (expandAll || article.articleId === expandedId)
                            ? cleanContent
                            : cleanContent.slice(0, 100)
                    }
                    keyword={keyword}
                />

                {!expandAll &&
                    article.articleId !== expandedId &&
                    cleanContent.length > 100 &&
                    '...'}
            </div>

            {!expandAll && cleanContent.length > 100 && (
                <button
                    className="more-button"
                    onClick={() => setExpandedId(
                        expandedId === article.articleId
                            ? null
                            : article.articleId
                    )}
                >
                    {expandedId === article.articleId ? '접기 ▲' : '더보기 ▼'}
                </button>
            )}
        </div>
    );
}

// 여기에 export 작성
export default ArticleCard;